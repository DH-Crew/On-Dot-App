package com.ondot.platform.util

import com.ondot.domain.service.ExternalAppLauncher
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosExternalAppLauncher : ExternalAppLauncher {
    override fun openEverytime() {
        val url = NSURL.URLWithString("everytime://") ?: return
        UIApplication.sharedApplication.openURL(url)
    }
}
