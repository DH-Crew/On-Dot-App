package com.ondot.platform.util

import android.content.Context
import android.content.Intent
import com.ondot.domain.service.ExternalAppLauncher
import androidx.core.net.toUri

class AndroidExternalAppLauncher(
    private val context: Context,
) : ExternalAppLauncher {
    override fun openEverytime() {
        val schemeIntent = Intent(
            Intent.ACTION_VIEW,
            "everytime://".toUri()
        ).apply {
            setPackage("com.everytime.v2")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val fallbackIntent = Intent(
            Intent.ACTION_VIEW,
            "everytime://".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        runCatching {
            context.startActivity(schemeIntent)
        }.getOrElse {
            runCatching {
                context.startActivity(fallbackIntent)
            }
        }
    }
}
