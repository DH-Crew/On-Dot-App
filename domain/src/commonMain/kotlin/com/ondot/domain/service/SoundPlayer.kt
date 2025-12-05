package com.ondot.domain.service

import com.ondot.domain.model.enums.AlarmMode

interface SoundPlayer {
    fun playSound(soundResId: String, alarmMode: AlarmMode = AlarmMode.SOUND, onComplete: () -> Unit = {})
    fun stopSound(onComplete: () -> Unit = {})
    fun setVolume(volume: Float)
}