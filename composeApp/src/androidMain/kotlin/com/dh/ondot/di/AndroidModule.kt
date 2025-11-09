package com.dh.ondot.di

import android.app.Application
import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.AutocaptureOption
import com.amplitude.android.Configuration
import com.amplitude.android.plugins.SessionReplayPlugin
import com.dh.ondot.composeApp.BuildKonfig
import org.koin.dsl.module

fun androidModule(app: Application) = module {
    single<Context> { app.applicationContext }
    single {
        Amplitude(
            Configuration(
                apiKey = BuildKonfig.AMPLITUDE_KEY,
                context = get<Context>(),
                autocapture = AutocaptureOption.ALL
            )
        ).apply {
            add(SessionReplayPlugin())
        }
    }
}