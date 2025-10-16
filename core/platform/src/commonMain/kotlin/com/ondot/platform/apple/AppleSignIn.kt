package com.ondot.platform.apple

expect fun appleSignIn(
    onSuccess: (identityToken: String?, authorizationCode: String?) -> Unit,
    onFailure: (Throwable) -> Unit = {}
)