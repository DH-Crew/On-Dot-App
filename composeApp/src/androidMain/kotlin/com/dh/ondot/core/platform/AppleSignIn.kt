package com.dh.ondot.core.platform

actual fun appleSignIn(
    onSuccess: (identityToken: String?, authorizationCode: String?) -> Unit,
    onFailure: (Throwable) -> Unit
) {} // android에서는 무시