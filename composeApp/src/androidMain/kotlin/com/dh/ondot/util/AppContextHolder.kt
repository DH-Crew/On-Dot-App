package com.dh.ondot.util

import android.app.Application
import android.content.Context

object AppContextHolder {
    private lateinit var _applicationContext: Context
    val context: Context
        get() = _applicationContext

    fun init(application: Application) {
        _applicationContext = application.applicationContext
    }
}