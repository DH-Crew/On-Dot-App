package com.dh.ondot.domain.di

interface SoundPlayer {
    fun playSound(soundResId: String, onComplete: () -> Unit = {})
    fun stopSound(onComplete: () -> Unit = {})
}