package com.dh.ondot.core.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.service.SoundPlayer
import com.dh.ondot.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmService : Service() {

    companion object {
        private const val CHANNEL_ID = "channel_alarm"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_START  = "com.dh.ondot.alarm.ACTION_START"
        const val ACTION_STOP   = "com.dh.ondot.alarm.ACTION_STOP"
    }

    private val logger = Logger.withTag("AlarmService")

    private lateinit var wakeLock: PowerManager.WakeLock

    private val storage by lazy { AndroidAlarmStorage(this) }
    private val soundPlayer: SoundPlayer = ServiceLocator.provideSoundPlayer()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_STOP -> {
                soundPlayer.stopSound()
                if (::wakeLock.isInitialized && wakeLock.isHeld) wakeLock.release()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_START -> {
                val alarmId = intent.getLongExtra("alarmId", -1L)
                val typeName = intent.getStringExtra("type")
                val type = typeName?.let { AlarmType.valueOf(it) } ?: AlarmType.Departure

                if (alarmId == -1L) {
                    logger.e { "Invalid extras, stopSelfResult($startId)" }
                    stopSelfResult(startId)
                    return START_NOT_STICKY
                }
                logger.d { "AlarmService onStartCommand: $alarmId, $type" }

                val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "OnDot:AlarmWakeLock")
                wakeLock.acquire(2 * 60 * 1000L)

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(CHANNEL_ID, "알람 재생 채널", NotificationManager.IMPORTANCE_HIGH).apply {
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(channel)

                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra("alarmId", alarmId)
                    putExtra("type", type.name)
                }
                val pendingIntent = PendingIntent.getActivity(this, alarmId.toInt(), mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("알람 재생 중")
                    .setContentText("잠시만 기다려주세요")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setFullScreenIntent(pendingIntent, true)
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .build()

                ServiceCompat.startForeground(
                    this,
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )

                startActivity(mainIntent)

                serviceScope.launch {
                    val alarmList = storage.alarmsFlow.first()
                    val alarm = alarmList.firstOrNull { it.alarmDetail.alarmId == alarmId }

                    logger.d { "Alarm RingTone: ${alarm?.alarmDetail?.ringTone?.lowercase()}" }

                    if (alarm != null && alarm.alarmDetail.enabled) {
                        soundPlayer.playSound(alarm.alarmDetail.ringTone.lowercase()) {
                            if (wakeLock.isHeld) wakeLock.release()
                            stopForeground(STOP_FOREGROUND_DETACH)
                            stopSelf()
                        }
                    } else {
                        stopSelf()
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        soundPlayer.stopSound()
        if (::wakeLock.isInitialized && wakeLock.isHeld) wakeLock.release()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
        super.onDestroy()
    }
}