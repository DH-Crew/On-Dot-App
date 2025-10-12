package com.dh.ondot.presentation.login

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.platform.appleSignIn
import com.dh.ondot.core.platform.kakaoSignIn
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.data.model.AuthTokens
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.model.response.AuthResponse
import com.dh.ondot.domain.repository.AuthRepository
import com.dh.ondot.presentation.ui.theme.ERROR_LOGIN
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository
): BaseViewModel<UiState.Default>(UiState.Default) {
    private val logger = Logger.withTag("LoginViewModel")

    fun performKakaoLogin() {
        kakaoSignIn { token ->
            if (token.isBlank()) return@kakaoSignIn

            viewModelScope.launch {
                authRepository.login("KAKAO", token).collect { result ->
                    resultResponse(result, ::onSuccessKakaoLogin, ::onFailedKakaoLogin)
                }
            }
        }
    }

    private fun onSuccessKakaoLogin(result: AuthResponse) {
        saveToken(token = AuthTokens(accessToken = result.accessToken, refreshToken = result.refreshToken))

        when(result.isNewMember) {
            true -> emitEventFlow(LoginEvent.NavigateToOnboarding)
            false -> emitEventFlow(LoginEvent.NavigateToMain)
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

    private fun onSuccessAppleLogin(result: AuthResponse) {
        saveToken(token = AuthTokens(accessToken = result.accessToken, refreshToken = result.refreshToken))

        when(result.isNewMember) {
            true -> emitEventFlow(LoginEvent.NavigateToOnboarding)
            false -> emitEventFlow(LoginEvent.NavigateToMain)
        }
    }

    private fun saveToken(token: AuthTokens) {
        viewModelScope.launch {
            authRepository.saveToken(token)
        }
    }
}