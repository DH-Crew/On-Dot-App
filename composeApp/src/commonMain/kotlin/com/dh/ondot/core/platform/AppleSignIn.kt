package com.dh.ondot.core.platform

expect fun appleSignIn(
    onSuccess: (identityToken: String?, authorizationCode: String?) -> Unit,
    onFailure: (Throwable) -> Unit = {}
)