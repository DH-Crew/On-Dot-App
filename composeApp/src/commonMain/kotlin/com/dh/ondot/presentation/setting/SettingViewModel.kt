package com.dh.ondot.presentation.setting

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.repository.AuthRepository
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.presentation.ui.theme.ERROR_LOGOUT
import com.dh.ondot.presentation.ui.theme.ERROR_WITHDRAW
import com.dh.ondot.presentation.ui.theme.LOGOUT_SUCCESS_MESSAGE
import com.dh.ondot.presentation.ui.theme.WITHDRAW_SUCCESS_MESSAGE
import kotlinx.coroutines.launch

class SettingViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository,
    private val memberRepository: MemberRepository = ServiceLocator.memberRepository
): BaseViewModel<SettingUiState>(SettingUiState()) {
    private val logger = Logger.withTag("SettingViewModel")

    /**--------------------------------------------로그아웃-----------------------------------------------*/

    fun toggleLogoutDialog() {
        updateState(uiState.value.copy(showLogoutDialog = !uiState.value.showLogoutDialog))
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect {
                resultResponse(it, ::onSuccessLogOut, ::onFailLogout)
            }
        }
    }

    private fun onSuccessLogOut(result: Unit) {
        viewModelScope.launch { ToastManager.show(LOGOUT_SUCCESS_MESSAGE, ToastType.INFO) }
        emitEventFlow(SettingEvent.NavigateToLoginScreen)
    }

    private fun onFailLogout(e: Throwable) {
        logger.e { "onFailLogout: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_LOGOUT, ToastType.ERROR) }
    }

    /**--------------------------------------------회원 탈퇴-----------------------------------------------*/

    fun withdrawUser() {
        viewModelScope.launch {
            memberRepository.withdrawUser().collect {
                resultResponse(it, ::onSuccessWithdraw, ::onFailWithdraw)
            }
        }
    }

    private fun onSuccessWithdraw(result: Unit) {
        viewModelScope.launch { ToastManager.show(WITHDRAW_SUCCESS_MESSAGE, ToastType.INFO) }
    }

    private fun onFailWithdraw(e: Throwable) {
        logger.e { "onFailWithdraw: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_WITHDRAW, ToastType.ERROR) }
    }

    /**--------------------------------------------상태 처리-----------------------------------------------*/

    // 탈퇴 버튼 활성화
    fun isButtonEnabled(): Boolean {
        return uiState.value.selectedWithdrawalReasonIndex != 5 || uiState.value.userInput.isNotEmpty()
    }
}