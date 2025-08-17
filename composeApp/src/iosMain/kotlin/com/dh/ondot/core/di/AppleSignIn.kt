package com.dh.ondot.core.di

import kotlinx.cinterop.BetaInteropApi
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.AuthenticationServices.ASPresentationAnchor
import platform.Foundation.NSArray
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.firstObject
import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

private object AppleAuthHolder {
    var delegate: AppleSignInDelegate? = null
    var controller: ASAuthorizationController? = null
}

private class AppleSignInDelegate(
    private val onSuccess: (String?, String?) -> Unit,
    private val onFailure: (Throwable) -> Unit
) : NSObject(), ASAuthorizationControllerDelegateProtocol, ASAuthorizationControllerPresentationContextProvidingProtocol {

    override fun authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization: ASAuthorization) {
        val credential = didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
        if (credential != null) {
            val idToken = credential.identityToken?.utf8OrBase64()
            val authCode = credential.authorizationCode?.utf8OrBase64()
            cleanup()
            onSuccess(idToken, authCode)
        } else {
            cleanup()
            onFailure(RuntimeException("No AppleID credential"))
        }
    }

    override fun authorizationController(controller: ASAuthorizationController, didCompleteWithError: NSError) {
        cleanup()
        onFailure(RuntimeException(didCompleteWithError.localizedDescription))
    }

    override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): ASPresentationAnchor {
        return currentPresentationAnchor() ?: ASPresentationAnchor()
    }

    private fun cleanup() {
        AppleAuthHolder.delegate = null
        AppleAuthHolder.controller = null
    }
}

@OptIn(BetaInteropApi::class)
private fun NSData.toUtf8String(): String = NSString.create(data = this, encoding = NSUTF8StringEncoding).toString()

private fun NSData.utf8OrBase64(): String = toUtf8String()

private fun currentPresentationAnchor(): ASPresentationAnchor {
    // 활성 씬 찾기
    val scene = UIApplication.sharedApplication.connectedScenes
        .firstOrNull { (it as? UIWindowScene)?.activationState == UISceneActivationStateForegroundActive }
            as? UIWindowScene

    // UIWindowScene.windows 는 Obj-C 배열(NSArray) → firstObject 사용 + 안전 캐스팅
    val winFromScene = (scene?.windows as? NSArray)?.firstObject as? UIWindow

    // 구버전/희귀 케이스 폴백
    return winFromScene ?: UIApplication.sharedApplication.keyWindow
}

actual fun appleSignIn(
    onSuccess: (identityToken: String?, authorizationCode: String?) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    dispatch_async(dispatch_get_main_queue()) {
        val provider = ASAuthorizationAppleIDProvider()
        val request = provider.createRequest().apply {
            requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)
        }

        val delegate = AppleSignInDelegate(onSuccess, onFailure)
        val controller = ASAuthorizationController(listOf(request)).apply {
            this.delegate = delegate               // weak → 강한 참조를 Holder에도 보관
            this.presentationContextProvider = delegate
        }

        AppleAuthHolder.delegate = delegate
        AppleAuthHolder.controller = controller

        controller.performRequests()
    }
}