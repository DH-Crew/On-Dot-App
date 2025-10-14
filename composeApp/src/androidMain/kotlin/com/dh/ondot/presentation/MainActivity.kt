package com.dh.ondot.presentation

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import co.touchlab.kermit.Logger
import com.dh.ondot.App
import com.dh.ondot.core.di.AndroidServiceLocator
import com.dh.ondot.core.util.AlarmNotifier
import com.dh.ondot.domain.RingingState
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.ui.AlarmEvent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val logger = Logger.withTag("MainActivity")
    private val dataStore by lazy { AndroidServiceLocator.provideDataStore() }
    private var lastNavKey: Pair<Long, Long>? = null

    /**
     * LOG: 인텐트 요약 출력
     * */
    private fun Intent?.dump(prefix: String = "intent"): String {
        if (this == null) return "$prefix=null"
        val keys = extras?.keySet()?.joinToString() ?: "extra 없음"
        val sid  = getLongExtra("scheduleId", -1L)
        val aid  = getLongExtra("alarmId", -1L)
        val type = getStringExtra("type")
        return "$prefix: action=$action, data=$data, extras=[$keys], scheduleId=$sid, alarmId=$aid, type=$type"
    }

    /**
     * Log: RingingState 출력
     * */
    private fun logRingingState(source: String, state: RingingState?) {
        logger.i { "[SoT][$source] state=$state (isRinging=${state?.isRinging}, scheduleId=${state?.scheduleId}, alarmId=${state?.alarmId}, type=${state?.type})" }
    }

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                logger.i { "알림 권한이 허용되었습니다." }
            } else {
                logger.w { "알림 권한이 거부되었습니다. 앱 설정으로 안내합니다." }
                startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                )
            }
        }

    private val exactAlarmLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val am = getSystemService(AlarmManager::class.java)
                if (am != null && !am.canScheduleExactAlarms()) {
                    Toast.makeText(
                        this,
                        "알람 권한이 필요합니다",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logger.i { "[Lifecycle] onCreate() start, ${intent.dump("launchIntent")}" }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val has = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!has) {
                logger.d { "알림 권한이 필요하여 런타임 요청을 시작합니다." }
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                logger.i { "알림 권한이 이미 허용된 상태입니다." }
            }
        }

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        enableEdgeToEdge()

        ensureExactAlarmPermission()

        /**
         * 알림 클릭으로 진입한 경우 현재 알람 재생 여부 확인 및 내비게이션
         * */
        handleAlarmIntent(intent)

        /**
         * 앱을 그냥 실행한 경우 현재 알람 재생 여부 확인 및 내비게이션
         * */
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dataStore.flow()
                    .distinctUntilChanged { old, new ->
                        old.isRinging == new.isRinging &&
                                old.scheduleId == new.scheduleId &&
                                old.alarmId == new.alarmId
                    }
                    .collect { state ->
                        if (state.isRinging) {
                            val key = state.scheduleId to state.alarmId
                            if (lastNavKey != key) {
                                lastNavKey = key
                                navigateToAlarm(state)
                            }
                        }
                    }
            }
        }

        setContent {
            Box(
                modifier = Modifier
                    .windowInsetsPadding(
                        WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
                    )
            ) {
                App()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        logger.i { "[Lifecycle] onNewIntent(): ${intent.dump("newIntent")}" }
        setIntent(intent)
        handleAlarmIntent(intent)
    }

    private fun ensureExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getSystemService(AlarmManager::class.java)
            if (am != null && !am.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:$packageName".toUri()
                }
                exactAlarmLauncher.launch(intent)
            }
        }
    }

    private fun handleAlarmIntent(intent: Intent?) {
        logger.i { "[INTENT] handleAlarmIntent(): ${intent.dump()}" }
        intent ?: return

        val sid  = intent.getLongExtra("scheduleId", -1L)
        val aid  = intent.getLongExtra("alarmId", -1L)
        val type = intent.getStringExtra("type") ?: return

        if (sid > 0 && aid > 0) {
            val t = AlarmType.valueOf(type)
            logger.i { "[NAV] navigateToAlarm via intent (sid=$sid, aid=$aid, type=$type)" }
            navigateToAlarm(RingingState(true, sid, aid, t))
        }
    }

    private fun navigateToAlarm(state: RingingState) {
        AlarmNotifier.notify(AlarmEvent(state.scheduleId, state.alarmId, state.type))
    }
}