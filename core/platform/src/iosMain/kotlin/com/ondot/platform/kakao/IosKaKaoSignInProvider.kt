package com.ondot.platform.kakao

import com.dh.ondot.kakaologin.KakaoLoginHelper
import com.ondot.domain.service.KaKaoSignInProvider
import kotlinx.cinterop.ExperimentalForeignApi

class IosKaKaoSignInProvider: KaKaoSignInProvider {
    @OptIn(ExperimentalForeignApi::class)
    override fun kakaoSignIn(onResult: (String) -> Unit) {
        val helper = KakaoLoginHelper()

        helper.login { accessToken, error ->
            if (error == null) {
                onResult(accessToken ?: "")
            } else {
                onResult("")
            }
        }
    }
}