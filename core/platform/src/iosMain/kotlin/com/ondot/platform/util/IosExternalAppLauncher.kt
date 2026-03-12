package com.ondot.platform.util

import com.ondot.domain.service.ExternalAppLauncher
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosExternalAppLauncher : ExternalAppLauncher {
    override fun openEverytime() {
        val url = NSURL.URLWithString("everytime://") ?: return

        UIApplication.sharedApplication.openURL(
            url = url,
            options = emptyMap<Any?, Any>(),
            completionHandler = { success ->
                if (!success) openEverytimeInAppStore()
            },
        )
    }

    private fun openEverytimeInAppStore() {
        val storeUrl =
            NSURL.URLWithString(
                "itms-apps://itunes.apple.com/app/id642416310",
            ) ?: return

        UIApplication.sharedApplication.openURL(
            storeUrl,
            options = emptyMap<Any?, Any>(),
            completionHandler = null,
        )
    }
}
