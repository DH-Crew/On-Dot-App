package com.dh.ondot.domain.service

interface SoundPlayer {
    fun playSound(soundResId: String, onComplete: () -> Unit = {})
    fun stopSound(onComplete: () -> Unit = {})
}