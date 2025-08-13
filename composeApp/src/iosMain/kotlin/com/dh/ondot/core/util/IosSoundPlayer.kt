package com.dh.ondot.core.util

import com.dh.ondot.domain.service.SoundPlayer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSBundle
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
class IosSoundPlayer: SoundPlayer {
    private var player: AVAudioPlayer? = null

    init {
        AVAudioSession.sharedInstance().setCategory(
            category = AVAudioSessionCategoryPlayback,
            error = null
        )
        AVAudioSession.sharedInstance().setActive(true, error = null)
    }

    override fun playSound(soundResId: String, onComplete: () -> Unit) {
        stopSound()

        val url: NSURL = NSBundle.mainBundle.URLForResource(
            name = soundResId,
            withExtension = "mp3"
        ) ?: run {
            println("resource를 찾을 수 없습니다.")
            return
        }

        player = AVAudioPlayer(contentsOfURL = url, error = null).also {
            it.numberOfLoops = 0
            it.prepareToPlay()
            it.play()
        }
    }

    override fun stopSound(onComplete: () -> Unit) {
        player?.apply {
            if (player?.playing == true) stop()
        }
        player = null
        onComplete()
    }

    override fun setVolume(volume: Float) {
        player?.volume = volume
    }
}