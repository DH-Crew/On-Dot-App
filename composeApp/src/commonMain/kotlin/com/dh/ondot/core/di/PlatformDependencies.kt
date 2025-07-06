package com.dh.ondot.core.di

import com.dh.ondot.domain.di.SoundPlayer
import com.dh.ondot.network.TokenProvider

expect fun provideTokenProvider(): TokenProvider

expect fun provideSoundPlayer(): SoundPlayer