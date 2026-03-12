package com.ondot.platform.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.ondot.domain.service.ExternalAppLauncher

class AndroidExternalAppLauncher(
    private val context: Context,
) : ExternalAppLauncher {
    override fun openEverytime() {
        val launchIntent =
            context.packageManager
                .getLaunchIntentForPackage(EVERYTIME_PACKAGE_NAME)
                ?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

        if (launchIntent != null) {
            context.startActivity(launchIntent)
            return
        }

        openEverytimeInPlayStore()
    }

    private fun openEverytimeInPlayStore() {
        val marketIntent =
            Intent(
                Intent.ACTION_VIEW,
                "market://details?id=$EVERYTIME_PACKAGE_NAME".toUri(),
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

        val webIntent =
            Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$EVERYTIME_PACKAGE_NAME".toUri(),
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

        runCatching {
            context.startActivity(marketIntent)
        }.getOrElse {
            runCatching {
                context.startActivity(webIntent)
            }
        }
    }

    private companion object {
        const val EVERYTIME_PACKAGE_NAME = "com.everytime.v2"
    }
}
