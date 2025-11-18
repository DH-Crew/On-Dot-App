package com.ondot.platform.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import co.touchlab.kermit.Logger
import com.dh.core.platform.R
import com.ondot.domain.model.enums.AlarmMode
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.domain.service.SoundPlayer
import com.ondot.platform.data.OnDotDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmService : Service(), KoinComponent {

    private val scheduleRepository: ScheduleRepository by inject()
    private val soundPlayer: SoundPlayer by inject()
    private val dataStore: OnDotDataStore by inject()

    companion object {
        private const val CHANNEL_ID = "channel_alarm"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_START  = "com.dh.ondot.alarm.ACTION_START"
        const val ACTION_STOP   = "com.dh.ondot.alarm.ACTION_STOP"
    }

    private val logger = Logger.withTag("AlarmService")

    private lateinit var wakeLock: PowerManager.WakeLock

    private var playJob: Job? = null
    private var playingAlarmId: Long? = null

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onBind(intent: Intent?): IBinder? = null

    private fun markRinging(scheduleId: Long, alarmId: Long, type: AlarmType) {
        val instanceId = System.currentTimeMillis()
        serviceScope.launch {
            dataStore.setRinging(scheduleId, alarmId, type, instanceId)
        }
    }

    private suspend fun clearRingingSync() {
        try {
            dataStore.clearRinging()
            logger.i { "[DS] clearRinging() OK" }
        } catch (e: Exception) {
            logger.e(e) { "[DS] clearRinging() FAIL: ${e.message}" }
        }
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_STOP -> {
                soundPlayer.stopSound()
                serviceScope.launch { stopAndClear() }
                return START_NOT_STICKY
            }
            ACTION_START -> {
                val scheduleId = intent.getLongExtra("scheduleId", -1L)
                val alarmId = intent.getLongExtra("alarmId", -1L)
                val typeName = intent.getStringExtra("type")
                val type = typeName?.let { AlarmType.valueOf(it) } ?: AlarmType.Departure

                if (alarmId == -1L) {
                    logger.e { "Invalid extras, stopSelfResult($startId)" }
                    stopSelfResult(startId)
                    return START_NOT_STICKY
                }
                logger.d { "AlarmService onStartCommand: $alarmId, $type" }

                val pm = getSystemService(POWER_SERVICE) as PowerManager

                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "OnDot:AlarmWakeLock")
                wakeLock.acquire(2 * 60 * 1000L)

                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(CHANNEL_ID, "알람 재생 채널", NotificationManager.IMPORTANCE_HIGH).apply {
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(channel)

                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra("scheduleId", scheduleId)
                    putExtra("alarmId", alarmId)
                    putExtra("type", type.name)
                }

                val pendingIntent = PendingIntent.getActivity(
                    this,
                    alarmId.toInt(),
                    launchIntent ?: Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_LAUNCHER)
                        setPackage(packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra("scheduleId", scheduleId)
                        putExtra("alarmId", alarmId)
                        putExtra("type", type.name)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("알람 재생 중")
                    .setContentText("잠시만 기다려주세요")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .build()

                ServiceCompat.startForeground(
                    this,
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )

                playingAlarmId = alarmId
                playJob?.cancel()
                playJob = serviceScope.launch {
                    scheduleRepository.getLocalScheduleById(scheduleId)
                        .map { schedule ->
                            schedule?.let {
                                if (type == AlarmType.Preparation) it.preparationAlarm else it.departureAlarm
                            }
                        }
                        .filterNotNull()
                        .take(1)
                        .collect { alarm ->
                            logger.d { "Alarm RingTone: ${alarm.ringTone.lowercase()}" }
                            if (alarm.enabled && alarm.alarmMode == AlarmMode.SOUND) {
                                markRinging(scheduleId, alarmId, type)

                                soundPlayer.playSound(alarm.ringTone.lowercase()) {
                                    serviceScope.launch { stopAndClear() }
                                }
                            } else {
                                serviceScope.launch { stopAndClear() }
                            }
                        }
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        soundPlayer.stopSound()
        runCatching { runBlocking { clearRingingSync() } }
        if (::wakeLock.isInitialized && wakeLock.isHeld) wakeLock.release()
        super.onDestroy()
    }

    private suspend fun stopAndClear() {
        if (::wakeLock.isInitialized && wakeLock.isHeld) wakeLock.release()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopForeground(STOP_FOREGROUND_REMOVE)
        clearRingingSync()
        stopSelf()
    }
}