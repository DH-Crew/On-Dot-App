package com.dh.ondot.presentation

import android.app.AlarmManager
import android.content.Intent
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
import androidx.core.net.toUri
import com.dh.ondot.App
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.ui.AlarmEvent

class MainActivity : ComponentActivity() {

    private var initialAlarmEvent: AlarmEvent? = null

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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

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
            initialAlarmEvent = event
        }
    }

    private fun parseAlarmEvent(intent: Intent): AlarmEvent? {
        val id = intent.getLongExtra("alarmId", -1L)
        val type = intent.getStringExtra("type")?.let { AlarmType.valueOf(it) }

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