package com.dh.ondot.presentation.login

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.di.kakaoSignIn
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.data.model.TokenModel
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.model.response.AuthResponse
import com.dh.ondot.domain.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository
): BaseViewModel<UiState.Default>(UiState.Default) {
    private val logger = Logger.withTag("LoginViewModel")

    fun onKakaoLogin() {
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
        saveToken(token = TokenModel(accessToken = result.accessToken, refreshToken = result.refreshToken))

        when(result.isNewMember) {
            true -> emitEventFlow(LoginEvent.NavigateToOnboarding)
            false -> emitEventFlow(LoginEvent.NavigateToMain)
        }
    }

    private fun onFailedKakaoLogin(e: Throwable) {
        viewModelScope.launch { ToastManager.show(message = "로그인에 실패하였습니다.", type = ToastType.ERROR) }
        logger.d { "throwable: ${e.message}" }
    }

    private fun saveToken(token: TokenModel) {
        viewModelScope.launch {
            authRepository.saveToken(token)
        }
    }
}