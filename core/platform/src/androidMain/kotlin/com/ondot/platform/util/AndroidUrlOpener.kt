package com.ondot.platform.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.ondot.domain.service.UrlOpener

class AndroidUrlOpener(
    private val context: Context
): UrlOpener {
    override fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}