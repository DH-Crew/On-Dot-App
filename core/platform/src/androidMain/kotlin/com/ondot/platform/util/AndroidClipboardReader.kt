package com.ondot.platform.util

import android.content.ClipboardManager
import android.content.Context
import com.ondot.domain.service.ClipboardReader

class AndroidClipboardReader(
    private val context: Context,
) : ClipboardReader {
    override suspend fun readText(): String? {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                ?: return null

        val primaryClip = clipboard.primaryClip ?: return null
        if (primaryClip.itemCount <= 0) return null

        return primaryClip.getItemAt(0).coerceToText(context)?.toString()
    }
}
