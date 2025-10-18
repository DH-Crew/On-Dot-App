package com.ondot.platform.kakao

import android.content.Context
import android.util.Log
import com.kakao.sdk.user.UserApiClient
import com.ondot.domain.service.KaKaoSignInProvider

class AndroidKaKaoSignInProvider(
    private val context: Context
): KaKaoSignInProvider {
    override fun kakaoSignIn(onResult: (String) -> Unit) {
        signInKakao(context, onResult)
    }

    private fun signInKakao(context: Context, onSuccessKaKaoLogin: (String) -> Unit) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            signInKakaoApp(context, onSuccessKaKaoLogin)
        } else {
            signInKakaoEmail(context, onSuccessKaKaoLogin)
        }
    }

    private fun signInKakaoApp(context: Context, onSuccessKaKaoLogin: (String) -> Unit) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e("Kakao Login Error", error.stackTraceToString())
                signInKakaoEmail(context, onSuccessKaKaoLogin)
                return@loginWithKakaoTalk
            }
            token?.let {
                onSuccessKaKaoLogin(token.accessToken)
            }
        }
    }

    private fun signInKakaoEmail(context: Context, onSuccessKaKaoLogin: (String) -> Unit) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            if (error != null) {
                Log.e("Kakao Login Error", error.stackTraceToString())
                return@loginWithKakaoAccount
            }
            token?.let {
                onSuccessKaKaoLogin(token.accessToken)
            }
        }
    }
}