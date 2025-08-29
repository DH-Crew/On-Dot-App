package com.dh.ondot.presentation.setting

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.model.request.DeleteAccountRequest
import com.dh.ondot.domain.model.request.settings.home_address.HomeAddressRequest
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.model.response.HomeAddressInfo
import com.dh.ondot.domain.repository.AuthRepository
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.repository.PlaceRepository
import com.dh.ondot.presentation.ui.theme.ERROR_GET_HOME_ADDRESS
import com.dh.ondot.presentation.ui.theme.ERROR_LOGOUT
import com.dh.ondot.presentation.ui.theme.ERROR_SEARCH_PLACE
import com.dh.ondot.presentation.ui.theme.ERROR_UPDATE_HOME_ADDRESS
import com.dh.ondot.presentation.ui.theme.ERROR_WITHDRAW
import com.dh.ondot.presentation.ui.theme.LOGOUT_SUCCESS_MESSAGE
import com.dh.ondot.presentation.ui.theme.WITHDRAW_SUCCESS_MESSAGE
import kotlinx.coroutines.launch

class SettingViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository,
    private val memberRepository: MemberRepository = ServiceLocator.memberRepository,
    private val placeRepository: PlaceRepository = ServiceLocator.placeRepository
): BaseViewModel<SettingUiState>(SettingUiState()) {
    private val logger = Logger.withTag("SettingViewModel")

    /**--------------------------------------------집 주소 설정-----------------------------------------------*/
    fun getHomeAddress() {
        viewModelScope.launch {
            memberRepository.getHomeAddress().collect {
                resultResponse(it, ::onSuccessGetHomeAddress, ::onFailGetHomeAddress)
            }
        }
    }

    private fun onSuccessGetHomeAddress(result: HomeAddressInfo) {
        updateState(uiState.value.copy(homeAddress = result))
    }

    private fun onFailGetHomeAddress(e: Throwable) {
        logger.e { "onFailGetHomeAddress: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_HOME_ADDRESS, ToastType.ERROR) }
    }

    fun queryHomeAddress(query: String) {
        viewModelScope.launch {
            placeRepository.searchPlace(query).collect {
                resultResponse(it, ::onSuccessSearchPlace, ::onFailSearchPlace)
            }
        }
    }

    private fun onSuccessSearchPlace(result: List<AddressInfo>) {
        updateState(uiState.value.copy(addressList = result))
    }

    private fun onFailSearchPlace(e: Throwable) {
        logger.e { "onFailSearchPlace: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_SEARCH_PLACE, ToastType.ERROR) }
    }

    fun setSelectedAddress(address: AddressInfo) {
        updateState(uiState.value.copy(selectedHomeAddress = address))
    }

    fun updateHomeAddress() {
        val newHomeAddress = HomeAddressInfo(
            roadAddress = uiState.value.selectedHomeAddress.roadAddress,
            latitude = uiState.value.selectedHomeAddress.latitude,
            longitude = uiState.value.selectedHomeAddress.longitude
        )

        updateState(uiState.value.copy(homeAddress = newHomeAddress))

        viewModelScope.launch {
            memberRepository.updateHomeAddress(
                request = HomeAddressRequest(
                    roadAddress = newHomeAddress.roadAddress,
                    latitude = newHomeAddress.latitude,
                    longitude = newHomeAddress.longitude
                )
            ).collect {
                resultResponse(it, ::onSuccessUpdateHomeAddress, ::onFailUpdateHomeAddress)
            }
        }
    }

    private fun onSuccessUpdateHomeAddress(result: Unit) {
        emitEventFlow(SettingEvent.PopScreen)
    }

    private fun onFailUpdateHomeAddress(e: Throwable) {
        logger.e { "onFailUpdateHomeAddress: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_UPDATE_HOME_ADDRESS, ToastType.ERROR) }
    }

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
        val selectedReason = uiState.value.accountDeletionReasons[uiState.value.selectedReasonIndex]

        viewModelScope.launch {
            memberRepository.withdrawUser(
                request = DeleteAccountRequest(withdrawalReasonId = selectedReason.id, customReason = "")
            ).collect {
                resultResponse(it, ::onSuccessWithdraw, ::onFailWithdraw)
            }
        }
    }

    private fun onSuccessWithdraw(result: Unit) {
        viewModelScope.launch { ToastManager.show(WITHDRAW_SUCCESS_MESSAGE, ToastType.INFO) }
        emitEventFlow(SettingEvent.NavigateToLoginScreen)
    }

    private fun onFailWithdraw(e: Throwable) {
        logger.e { "onFailWithdraw: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_WITHDRAW, ToastType.ERROR) }
    }

    fun onReasonClick(index: Int) {
        updateState(uiState.value.copy(selectedReasonIndex = index))
    }

    fun toggleDeleteAccountDialog() {
        updateState(uiState.value.copy(showDeleteAccountDialog = !uiState.value.showDeleteAccountDialog))
    }

    /**--------------------------------------------상태 처리-----------------------------------------------*/

    // 탈퇴 버튼 활성화
    fun isButtonEnabled(): Boolean {
        return uiState.value.selectedReasonIndex != 5 || uiState.value.userInput.isNotEmpty()
    }
}