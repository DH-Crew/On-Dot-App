package com.dh.ondot.core.util

import com.dh.ondot.domain.di.SoundPlayer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSBundle
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
class IosSoundPlayer: SoundPlayer {
    private var player: AVAudioPlayer? = null

    override fun playSound(soundResId: String) {
        stopSound()

        val url: NSURL? = NSBundle.mainBundle.URLForResource(
            name = soundResId,
            withExtension = "mp3"
        )
        if (url != null) {
            player = AVAudioPlayer(contentsOfURL = url, error = null).also {
                it.numberOfLoops = 0
                it.prepareToPlay()
                it.play()
            }
        }
    }

    override fun stopSound() {
        player?.apply {
            if (player?.playing == true) stop()
        }
        player = null
    }
}