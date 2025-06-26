package com.dh.ondot.app

import android.app.Application
import com.dh.ondot.util.AppContextHolder

class OnDotApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextHolder.init(this)
    }
}