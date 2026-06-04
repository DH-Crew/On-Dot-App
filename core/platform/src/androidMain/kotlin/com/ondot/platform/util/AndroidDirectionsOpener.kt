package com.ondot.platform.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.TransportType
import com.ondot.domain.service.DirectionsOpener

class AndroidDirectionsOpener(
    private val context: Context,
) : DirectionsOpener {
    override fun openDirections(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        provider: MapProvider,
        startName: String,
        endName: String,
        transportType: TransportType,
    ) {
        val mode =
            when (provider) {
                MapProvider.KAKAO -> transportType.toKakaoRouteMode()
                MapProvider.NAVER -> transportType.toNaverRouteMode()
                MapProvider.APPLE -> error("unreachable")
            }
        val intent =
            when (provider) {
                MapProvider.KAKAO ->
                    Intent(
                        Intent.ACTION_VIEW,
                        "kakaomap://route?sp=$startLat,$startLng&ep=$endLat,$endLng&by=$mode".toUri(),
                    ).apply { `package` = "net.daum.android.map" }
                MapProvider.NAVER ->
                    Intent(
                        Intent.ACTION_VIEW,
                        (
                            "nmap://route/$mode" +
                                "?slat=$startLat&slng=$startLng&sname=${Uri.encode(startName)}" +
                                "&dlat=$endLat&dlng=$endLng&dname=${Uri.encode(endName)}" +
                                "&appname=${context.packageName}"
                        ).toUri(),
                    ).apply { `package` = "com.nhn.android.nmap" }
                MapProvider.APPLE -> error("unreachable")
            }

        val pm: PackageManager = context.packageManager
        val canHandle = intent.resolveActivity(pm) != null

        Logger.e { "canHandle: $canHandle" }

        if (canHandle) {
            runCatching {
                context.startActivity(intent.addCategory(Intent.CATEGORY_BROWSABLE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }.onFailure {
                Logger.w { "지도 앱 실행 실패. 스토어로 fallback합니다." }

                val storeHttp =
                    when (provider) {
                        MapProvider.KAKAO -> "https://play.google.com/store/apps/details?id=net.daum.android.map"
                        MapProvider.NAVER -> "https://play.google.com/store/apps/details?id=com.nhn.android.nmap"
                        MapProvider.APPLE -> error("unreachable")
                    }

                context.startActivity(Intent(Intent.ACTION_VIEW, storeHttp.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        } else {
            val storeUri =
                when (provider) {
                    MapProvider.KAKAO -> "market://details?id=net.daum.android.map"
                    MapProvider.NAVER -> "market://details?id=com.nhn.android.nmap"
                    MapProvider.APPLE -> error("unreachable")
                }
            val started =
                runCatching {
                    context.startActivity(Intent(Intent.ACTION_VIEW, storeUri.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }.isSuccess

            if (!started) {
                val storeHttp = storeUri.replace("market://details?id=", "https://play.google.com/store/apps/details?id=")
                context.startActivity(Intent(Intent.ACTION_VIEW, storeHttp.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }

    private fun TransportType.toKakaoRouteMode(): String =
        when (this) {
            TransportType.PUBLIC_TRANSPORT -> "PUBLICTRANSIT"
            TransportType.CAR -> "CAR"
        }

    private fun TransportType.toNaverRouteMode(): String =
        when (this) {
            TransportType.PUBLIC_TRANSPORT -> "public"
            TransportType.CAR -> "car"
        }
}
