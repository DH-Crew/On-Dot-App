package com.ondot.platform.util

import com.ondot.domain.service.ClipboardReader
import platform.UIKit.UIPasteboard

class IosClipboardReader : ClipboardReader {
    override suspend fun readText(): String? = UIPasteboard.generalPasteboard.string
}
