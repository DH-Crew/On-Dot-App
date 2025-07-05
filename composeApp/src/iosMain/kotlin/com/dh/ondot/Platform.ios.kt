package com.dh.ondot

import com.dh.ondot.presentation.ui.theme.IOS
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = IOS
}

actual fun getPlatform(): Platform = IOSPlatform()