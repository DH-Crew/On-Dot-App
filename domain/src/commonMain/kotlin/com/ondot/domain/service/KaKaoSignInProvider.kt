package com.ondot.domain.service

interface KaKaoSignInProvider {
    fun kakaoSignIn(onResult: (String) -> Unit)
}