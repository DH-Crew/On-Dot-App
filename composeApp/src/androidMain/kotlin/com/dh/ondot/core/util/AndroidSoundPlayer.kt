package com.dh.ondot.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import com.dh.ondot.domain.di.SoundPlayer

class AndroidSoundPlayer(
    private val context: Context
) : SoundPlayer {
    private var player: MediaPlayer? = null

    @SuppressLint("DiscouragedApi")
    override fun playSound(soundResId: String) {
        stopSound()

        // raw 폴더에 있는 리소스 이름을 sound_1.mp3 → R.raw.sound_1로 변환하는 메서드
        val resId = context.resources.getIdentifier(
            soundResId, "raw", context.packageName
        ).takeIf { it != 0 } ?: return

        player = MediaPlayer.create(context, resId)?.apply {
            isLooping = false
            start()
        }
    }

    override fun stopSound() {
        player?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        player = null
    }
}