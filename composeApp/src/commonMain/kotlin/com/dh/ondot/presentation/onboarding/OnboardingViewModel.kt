package com.dh.ondot.presentation.onboarding

import androidx.lifecycle.viewModelScope
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.di.provideSoundPlayer
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.domain.di.SoundPlayer
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.repository.PlaceRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val placeRepository: PlaceRepository = ServiceLocator.placeRepository,
    private val soundPlayer: SoundPlayer = provideSoundPlayer()
): BaseViewModel<OnboardingUiState>(OnboardingUiState()) {
    // 온보딩 단계가 초기화되지 않은 경우 초기화하는 메서드
    fun initStep() {
        updateState(uiState.value.copy(currentStep = 1, totalStep = 5))
    }

    // 다음 버튼의 활성화 여부를 판단하는 메서드
    fun isButtonEnabled(): Boolean {
        return when (uiState.value.currentStep) {
            1 -> {
                val hour = uiState.value.hourInput.toIntOrNull() ?: 0
                val minute = uiState.value.minuteInput.toIntOrNull() ?: 0
                hour >= 0 || minute >= 0
            }
            2 -> {
                uiState.value.selectedAddress != null
            }
            3 -> {
                uiState.value.isMuted || uiState.value.selectedSound != null
            }
            else -> {
                false
            }
        }
    }

    // 다음 버튼을 클릭했을 때 호출되는 메서드
    fun onClickNext() {
        when (uiState.value.currentStep) {
            1 -> {
                updateState(uiState.value.copy(currentStep = 2))
                calculatePreparationTime()
            }
            2 -> {
                updateState(uiState.value.copy(currentStep = 3))
            }
            3 -> {
                updateState(uiState.value.copy(currentStep = 4))
            }
            4 -> {
                updateState(uiState.value.copy(currentStep = 5))
            }
            5 -> {
                // TODO: 온보딩 완료 로직
            }
        }
    }

    // ---------------------------------------- OnboardingStep1 ----------------------------

    // hourInput의 변화를 반영하는 콜백 메서드
    fun onHourInputChanged(hourInput: String) {
        updateState(uiState.value.copy(hourInput = hourInput))
    }

    // minuteInput의 변화를 반영하는 콜백 메서드
    fun onMinuteInputChanged(minuteInput: String) {
        updateState(uiState.value.copy(minuteInput = minuteInput))
    }

    private fun calculatePreparationTime() {
        val hour = uiState.value.hourInput.toIntOrNull()?.coerceAtLeast(0) ?: 0
        val minute = uiState.value.minuteInput.toIntOrNull()?.coerceAtLeast(0) ?: 0

        val totalMinutes = hour * 60 + minute
        updateState(uiState.value.copy(preparationTime = totalMinutes))
    }

    // ----------------------------------------- OnboardingStep2 ----------------------------

    // 검색어에 따른 장소 검색 API 호출 메서드
    fun onAddressInputChanged(input: String) {
        updateState(uiState.value.copy(addressInput = input))
        searchPlace(input)
    }

    private fun searchPlace(query: String) {
        if (query.isBlank()) {
            updateState(uiState.value.copy(addressList = emptyList()))
            return
        }

        viewModelScope.launch {
            placeRepository.searchPlace(query).collect {
                resultResponse(it, ::onSuccessSearchPlace)
            }
        }
    }

    private fun onSuccessSearchPlace(result: List<AddressInfo>) {
        updateState(uiState.value.copy(addressList = result))
    }

    fun onClickPlace(info: AddressInfo) {
        updateState(
            uiState.value.copy(
                addressInput = info.title,
                selectedAddress = info,
                addressList = emptyList()
            )
        )
    }

    // ----------------------------------------- OnboardingStep3 ----------------------------

    fun onToggleMute(newValue: Boolean) {
        updateState(uiState.value.copy(isMuted = newValue))
        if (newValue) { soundPlayer.stopSound() }
    }

    fun onCategorySelected(newIndex: Int) {
        updateState(
            uiState.value.copy(
                selectedCategoryIndex = newIndex,
                filteredSounds = uiState.value.sounds.filter { it.category == uiState.value.categories[newIndex] }
            )
        )
    }

    fun onSelectSound(newSoundId: String) {
        updateState(uiState.value.copy(selectedSound = newSoundId))
//        soundPlayer.playSound(newSoundId)
    }

    fun onVolumeChange(newVolume: Float) {
        updateState(uiState.value.copy(volume = newVolume))
    }
}