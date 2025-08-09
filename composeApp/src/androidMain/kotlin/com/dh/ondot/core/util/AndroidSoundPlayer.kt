package com.dh.ondot.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import co.touchlab.kermit.Logger
import com.dh.ondot.domain.service.SoundPlayer

class AndroidSoundPlayer(
    private val context: Context
) : SoundPlayer {
    private val logger = Logger.withTag("AndroidSoundPlayer")
    private var player: MediaPlayer? = null
    private lateinit var audioFocusRequest: AudioFocusRequest
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    @SuppressLint("DiscouragedApi")
    override fun playSound(soundResId: String, onComplete: () -> Unit) {
        // 이전 재생 정리
        stopSound()

        // AudioAttributes 생성 (알람용)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        // AudioFocusRequest 생성
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(audioAttributes)
            .setOnAudioFocusChangeListener { /* 필요시 포커스 변화 처리 */ }
            .build()

        // 오디오 포커스 요청
        val focusResult = audioManager.requestAudioFocus(audioFocusRequest)
        if (focusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            logger.w { "오디오 포커스 획득 실패: $focusResult" }
        }

        // raw 리소스 ID 조회
        val resId = context.resources.getIdentifier(
            soundResId, "raw", context.packageName
        )
        if (resId == 0) {
            logger.e { "raw 리소스 '$soundResId' 를 찾을 수 없습니다." }
            return
        }

        // MediaPlayer 직접 구성 및 재생
        try {
            player = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)

                // 정확한 offset/length 사용
                val afd = context.resources.openRawResourceFd(resId)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()

                isLooping = true  // 알람은 반복 재생
                setOnCompletionListener {
                    // 재생 완료 시 포커스 반환 후 정리
                    audioManager.abandonAudioFocusRequest(audioFocusRequest)
                    stopSound()
                    onComplete()
                }

                setVolume(1f, 1f)

                prepare()
                start()
            }
        } catch (e: Exception) {
            logger.e { "사운드 재생 중 오류: ${e.message}" }
        }
    }

    override fun stopSound(onComplete: () -> Unit) {
        // 중간 정지 시에도 포커스 반환
        if (::audioFocusRequest.isInitialized) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        }

        player?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        player = null
        onComplete()
    }
}