package com.dh.ondot

import com.dh.ondot.presentation.ui.theme.IOS

class IOSPlatform : Platform {
    override val name: String = IOS
}

actual fun getPlatform(): Platform = IOSPlatform()
