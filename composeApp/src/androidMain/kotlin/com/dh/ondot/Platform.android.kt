package com.dh.ondot

import android.os.Build
import com.dh.ondot.presentation.ui.theme.ANDROID

class AndroidPlatform : Platform {
    override val name: String = ANDROID
}

actual fun getPlatform(): Platform = AndroidPlatform()