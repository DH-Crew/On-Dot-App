package com.dh.ondot.presentation.setting

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.domain.model.enums.MapProvider
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.model.request.DeleteAccountRequest
import com.dh.ondot.domain.model.request.MapProviderRequest
import com.dh.ondot.domain.model.request.settings.home_address.HomeAddressRequest
import com.dh.ondot.domain.model.request.settings.preparation_time.PreparationTimeRequest
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.model.response.HomeAddressInfo
import com.dh.ondot.domain.repository.AuthRepository
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.repository.PlaceRepository
import com.dh.ondot.presentation.ui.theme.ERROR_GET_HOME_ADDRESS
import com.dh.ondot.presentation.ui.theme.ERROR_LOGOUT
import com.dh.ondot.presentation.ui.theme.ERROR_SEARCH_PLACE
import com.dh.ondot.presentation.ui.theme.ERROR_SET_MAP_PROVIDER
import com.dh.ondot.presentation.ui.theme.ERROR_UPDATE_HOME_ADDRESS
import com.dh.ondot.presentation.ui.theme.ERROR_UPDATE_PREPARATION_TIME
import com.dh.ondot.presentation.ui.theme.ERROR_WITHDRAW
import com.dh.ondot.presentation.ui.theme.LOGOUT_SUCCESS_MESSAGE
import com.dh.ondot.presentation.ui.theme.WITHDRAW_SUCCESS_MESSAGE
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository,
    private val memberRepository: MemberRepository = ServiceLocator.memberRepository,
    private val placeRepository: PlaceRepository = ServiceLocator.placeRepository
): BaseViewModel<SettingUiState>(SettingUiState()) {
    private val logger = Logger.withTag("SettingViewModel")
    private var searchJob: Job? = null

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
        if (query.isBlank()) {
            updateState(uiState.value.copy(addressList = emptyList()))
            return
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(200)
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
        updateState(
            uiState.value.copy(
                homeAddress = HomeAddressInfo(
                    roadAddress = uiState.value.selectedHomeAddress.roadAddress,
                    latitude = uiState.value.selectedHomeAddress.latitude,
                    longitude = uiState.value.selectedHomeAddress.longitude
                )
            )
        )

        emitEventFlow(SettingEvent.PopScreen)
    }

    private fun onFailUpdateHomeAddress(e: Throwable) {
        logger.e { "onFailUpdateHomeAddress: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_UPDATE_HOME_ADDRESS, ToastType.ERROR) }
    }

    /**--------------------------------------------길 안내 지도 설정-----------------------------------------------*/

    fun getUserMapProvider() {
        viewModelScope.launch {
            memberRepository.getLocalMapProvider().collect {
                updateSelectedProvider(it)
            }
        }
    }

    fun updateSelectedProvider(newProvider: MapProvider) {
        updateState(uiState.value.copy(selectedProvider = newProvider))
    }

    fun updateMapProvider() {
        viewModelScope.launch {
            memberRepository.updateMapProvider(
                request = MapProviderRequest(mapProvider = uiState.value.selectedProvider)
            ).collect {
                resultResponse(it, ::onSuccessUpdateMapProvider, ::onFailUpdateMapProvider)
            }
        }
    }

    private fun onSuccessUpdateMapProvider(result: Unit) {
        viewModelScope.launch { memberRepository.setLocalMapProvider(uiState.value.selectedProvider) }

        emitEventFlow(SettingEvent.PopScreen)
    }

    private fun onFailUpdateMapProvider(e: Throwable) {
        logger.e { "onFailUpdateMapProvider: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_SET_MAP_PROVIDER, ToastType.ERROR) }
    }

    /**--------------------------------------------길 안내 지도 설정-----------------------------------------------*/

    fun onHourInputChanged(newHourInput: String) {
        updateState(uiState.value.copy(hourInput = newHourInput))
    }

    fun onMinuteInputChanged(newMinuteInput: String) {
        updateState(uiState.value.copy(minuteInput = newMinuteInput))
    }

    fun updatePreparationTime() {
        val hours = uiState.value.hourInput.trim().toIntOrNull() ?: 0
        val minutes = uiState.value.minuteInput.trim().toIntOrNull() ?: 0
        val preparationTime = hours + minutes

        viewModelScope.launch {
            memberRepository.updatePreparationTime(request = PreparationTimeRequest(preparationTime)).collect {
                resultResponse(it, ::onSuccessUpdatePreparationTime, ::onFailUpdatePreparationTime)
            }
        }
    }

    private fun onSuccessUpdatePreparationTime(result: Unit) {
        emitEventFlow(SettingEvent.PopScreen)
    }

    private fun onFailUpdatePreparationTime(e: Throwable) {
        logger.e { "onFailUpdatePreparationTime: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_UPDATE_PREPARATION_TIME, ToastType.ERROR) }
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