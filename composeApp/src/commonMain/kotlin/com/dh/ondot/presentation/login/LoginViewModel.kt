package com.dh.ondot.presentation.login

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.di.kakaoSignIn
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.data.model.TokenModel
import com.dh.ondot.domain.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository
): BaseViewModel<UiState.Default>(UiState.Default) {
    private val logger = Logger.withTag("LoginViewModel")

    fun onKakaoLogin() {
        kakaoSignIn { token ->
            viewModelScope.launch {
                authRepository.login("KAKAO", token).collect { result ->
                    result.onSuccess { authResponse ->
                        saveToken(token = TokenModel(accessToken = authResponse.accessToken, refreshToken = authResponse.refreshToken))
                        emitEventFlow(LoginEvent.NavigateToOnboarding)
                    }
                    result.onFailure { throwable ->
                        logger.d { "throwable: $throwable" }
                    }
                }
            }
        }
    }

    private fun saveToken(token: TokenModel) {
        viewModelScope.launch {
            authRepository.saveToken(token)
        }
    }
}