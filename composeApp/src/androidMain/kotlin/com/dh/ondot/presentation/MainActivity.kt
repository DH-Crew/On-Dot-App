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
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import com.dh.ondot.App
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.ui.AlarmEvent

class MainActivity : ComponentActivity() {

    private val logger = Logger.withTag("MainActivity")

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

    private var initialAlarmEvent by mutableStateOf<AlarmEvent?>(null)

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

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        initialAlarmEvent = parseAlarmEvent(intent)

        setContent {
            Box(
                modifier = Modifier
                    .windowInsetsPadding(
                        WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
                    )
            ) {
                App(initialAlarm = initialAlarmEvent)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        parseAlarmEvent(intent)?.let { event ->
            logger.d { "onNewIntent: $event" }
            initialAlarmEvent = event
        }
    }

    private fun parseAlarmEvent(intent: Intent): AlarmEvent? {
        val id = intent.getLongExtra("alarmId", -1L)
        val type = intent.getStringExtra("type")?.let {
            try {
                AlarmType.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        return if (id != -1L && type != null) { AlarmEvent(id, type) } else null
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
}