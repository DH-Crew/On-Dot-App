package com.ondot.platform.util

import com.ondot.domain.service.UrlOpener
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosUrlOpener: UrlOpener {
    override fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        UIApplication.sharedApplication.openURL(nsUrl, emptyMap<Any?, Any?>(), completionHandler = null)
    }
}