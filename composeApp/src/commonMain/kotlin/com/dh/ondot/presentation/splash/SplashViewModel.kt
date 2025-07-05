package com.dh.ondot.presentation.splash

import androidx.lifecycle.ViewModel
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.network.TokenProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SplashViewModel(
    val tokenProvider: TokenProvider = ServiceLocator.provideTokenProvider()
): ViewModel() {
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()


}