package com.dh.ondot.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.dh.ondot.BuildConfig
import com.dh.ondot.core.initKoin
import com.dh.ondot.di.androidModule
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk

class OnDotApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        initKoin(listOf(androidModule(this)))
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleLogger)
    }
}