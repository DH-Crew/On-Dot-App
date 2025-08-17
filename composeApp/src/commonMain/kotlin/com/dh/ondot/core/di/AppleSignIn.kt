package com.dh.ondot.core.di

expect fun appleSignIn(
    onSuccess: (identityToken: String?, authorizationCode: String?) -> Unit,
    onFailure: (Throwable) -> Unit = {}
)