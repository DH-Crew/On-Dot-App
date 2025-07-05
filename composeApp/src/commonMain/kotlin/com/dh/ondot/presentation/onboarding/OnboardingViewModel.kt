package com.dh.ondot.presentation.onboarding

import com.dh.ondot.core.ui.base.BaseViewModel

class OnboardingViewModel(

): BaseViewModel<OnboardingUiState>(OnboardingUiState()) {
    // 온보딩 단계가 초기화되지 않은 경우 초기화하는 메서드
    fun initStep() {
        updateState(uiState.value.copy(currentStep = 1, totalStep = 5))
    }

    // 다음 버튼의 활성화 여부를 판단하는 메서드
    fun isButtonEnabled(): Boolean {
        return when (uiState.value.currentStep) {
            1 -> {
                uiState.value.hourInput.isNotEmpty() || uiState.value.minuteInput.isNotEmpty()
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

    // ------------------------- OnboardingStep1 ----------------------------

    // hourInput의 변화를 반영하는 콜백 메서드
    fun onHourInputChanged(hourInput: String) {
        updateState(uiState.value.copy(hourInput = hourInput))
    }

    // minuteInput의 변화를 반영하는 콜백 메서드
    fun onMinuteInputChanged(minuteInput: String) {
        updateState(uiState.value.copy(minuteInput = minuteInput))
    }
}