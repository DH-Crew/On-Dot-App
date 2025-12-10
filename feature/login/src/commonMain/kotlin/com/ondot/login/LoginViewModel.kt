package com.ondot.login

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.presentation.ui.theme.ERROR_LOGIN
import com.ondot.domain.model.auth.AuthResult
import com.ondot.domain.model.auth.AuthTokens
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.repository.AuthRepository
import com.ondot.domain.service.AnalyticsManager
import com.ondot.domain.service.KaKaoSignInProvider
import com.ondot.platform.apple.appleSignIn
import com.ondot.ui.base.BaseViewModel
import com.ondot.ui.base.UiState
import com.ondot.ui.util.ToastManager
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val kaKaoSignInProvider: KaKaoSignInProvider,
    private val analyticsManager: AnalyticsManager
): BaseViewModel<UiState.Default>(UiState.Default) {
    private val logger = Logger.withTag("LoginViewModel")

    fun performKakaoLogin() {
        kaKaoSignInProvider.kakaoSignIn { token ->
            if (token.isBlank()) return@kakaoSignIn

            viewModelScope.launch {
                authRepository.login("KAKAO", token).collect { result ->
                    resultResponse(result, ::onSuccessKakaoLogin, ::onFailedKakaoLogin)
                }
            }
        }
    }

    private fun onSuccessKakaoLogin(result: AuthResult) {
        viewModelScope.launch {
            val tokens = AuthTokens(
                accessToken = result.tokens.accessToken,
                refreshToken = result.tokens.refreshToken
            )

            saveToken(tokens)

            if (result.memberId > 0) {
                analyticsManager.setUserId(result.memberId.toString())
            }

            if (result.isNewMember) {
                emitEventFlow(LoginEvent.NavigateToOnboarding)
            } else {
                emitEventFlow(LoginEvent.NavigateToMain)
            }
        }
    }

    private fun onFailedKakaoLogin(e: Throwable) {
        viewModelScope.launch { ToastManager.show(message = ERROR_LOGIN, type = ToastType.ERROR) }
        logger.d { "throwable: ${e.message}" }
    }

    fun performAppleLogin() {
        appleSignIn(
            onSuccess = { identityToken, authorizationCode ->
                if (identityToken?.isBlank() == true || authorizationCode?.isBlank() == true) return@appleSignIn

                authorizationCode?.let {
                    viewModelScope.launch {
                        authRepository.login("APPLE", authorizationCode).collect { result ->
                            resultResponse(result, ::onSuccessAppleLogin)
                        }
                    }
                }
            },
            onFailure = {
                viewModelScope.launch { ToastManager.show(message = ERROR_LOGIN, type = ToastType.ERROR) }
                logger.e { "throwable: ${it.message}" }
            }
        )
    }

    private fun onSuccessAppleLogin(result: AuthResult) {
        viewModelScope.launch {
            val tokens = AuthTokens(
                accessToken = result.tokens.accessToken,
                refreshToken = result.tokens.refreshToken
            )

            saveToken(tokens)

            if (result.memberId > 0) {
                analyticsManager.setUserId(result.memberId.toString())
            }

            if (result.isNewMember) {
                emitEventFlow(LoginEvent.NavigateToOnboarding)
            } else {
                emitEventFlow(LoginEvent.NavigateToMain)
            }
        }
    }

    private suspend fun saveToken(token: AuthTokens) {
        authRepository.saveToken(token)
    }

    private fun setUserId(id: Long) {
        analyticsManager.setUserId(id.toString())
    }
}