package com.dh.ondot.app

import android.app.Application
import com.dh.ondot.BuildConfig
import com.dh.ondot.core.initKoin
import com.dh.ondot.core.initShared
import com.dh.ondot.util.AppContextHolder
import com.kakao.sdk.common.KakaoSdk

class OnDotApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextHolder.init(this)
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        initShared()
        initKoin()
    }
}