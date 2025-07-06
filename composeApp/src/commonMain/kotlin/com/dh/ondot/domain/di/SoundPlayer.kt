package com.dh.ondot.domain.di

interface SoundPlayer {
    fun playSound(soundResId: String)
    fun stopSound()
}