package com.dh.ondot.presentation.splash

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.data.model.TokenModel
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.repository.AuthRepository
import com.dh.ondot.core.network.TokenProvider
import com.dh.ondot.presentation.ui.theme.ERROR_LOGIN
import kotlinx.coroutines.launch

class SplashViewModel(
    private val tokenProvider: TokenProvider = ServiceLocator.provideTokenProvider(),
    private val authRepository: AuthRepository = ServiceLocator.authRepository
): BaseViewModel<SplashUiState>(SplashUiState()) {
    private val logger = Logger.withTag("SplashViewModel")

    init {
        checkForLocalToken()
    }

    private fun checkForLocalToken() {
        viewModelScope.launch {
            val tokenModel = tokenProvider.getToken()

            if (tokenModel == null) {
                updateState(uiState.value.copy(skipLogin = false))
            } else {
                refreshToken()
            }
        }
    }

    private fun refreshToken() {
        viewModelScope.launch {
            authRepository.reissueToken().collect {
                resultResponse(it, ::onSuccessReissueToken, ::onFailReissueToken)
            }
        }
    }

    private fun onSuccessReissueToken(result: TokenModel) {
        viewModelScope.launch {
            tokenProvider.saveToken(result)
            updateState(uiState.value.copy(skipLogin = true))
        }
    }

    private fun onFailReissueToken(e: Throwable) {
        logger.e { "토큰 갱신 실패: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_LOGIN, ToastType.ERROR) }
    }
}