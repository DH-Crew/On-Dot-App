package com.dh.ondot.core.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import co.touchlab.kermit.Logger
import com.dh.ondot.R
import com.dh.ondot.core.di.provideSoundPlayer
import com.dh.ondot.domain.service.SoundPlayer
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmService : Service() {

    companion object {
        private const val CHANNEL_ID = "alarm_channel"
        private const val NOTIFICATION_ID = 1001
    }


    private val logger = Logger.withTag("AlarmService")

    private lateinit var wakeLock: PowerManager.WakeLock

    private val storage by lazy { AndroidAlarmStorage(this) }
    private val soundPlayer: SoundPlayer by lazy { provideSoundPlayer() }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getLongExtra("alarmId", -1L) ?: return START_NOT_STICKY
        val typeName = intent.getStringExtra("type")
        val type = typeName?.let { AlarmType.valueOf(it) } ?: AlarmType.Departure

        logger.d { "AlarmService onStartCommand: $alarmId, $type" }

        val chan = NotificationChannel(
            CHANNEL_ID,
            "알람 재생 채널",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "알람이 울리는 동안 표시되는 채널"
        }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(chan)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("알람 재생 중")
            .setContentText("잠시만 기다려주세요")
            .setOngoing(true)
            .build()

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "OnDot:AlarmWakeLock"
        )
        wakeLock.acquire(10 * 60 * 1000L)

        serviceScope.launch {
            val alarmList = storage.alarmsFlow.first()
            val alarm = alarmList.firstOrNull { it.alarmId == alarmId }

            logger.d { "Alarm RingTone: ${alarm?.ringTone?.lowercase()}" }

            if (alarm != null && alarm.enabled) {
                soundPlayer.playSound(alarm.ringTone.lowercase()) {
                    if (wakeLock.isHeld) wakeLock.release()
                    stopForeground(STOP_FOREGROUND_DETACH)
                    stopSelf()
                }

                val activityIntent = Intent(this@AlarmService, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

                    putExtra("alarmId", alarmId)
                    putExtra("alarmType", type.name)
                }

                startActivity(activityIntent)
            } else {
                stopSelf()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        soundPlayer.stopSound()
        if (::wakeLock.isInitialized && wakeLock.isHeld) wakeLock.release()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
        super.onDestroy()
    }
}