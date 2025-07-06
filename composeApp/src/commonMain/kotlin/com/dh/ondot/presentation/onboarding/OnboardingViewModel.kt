package com.dh.ondot.presentation.onboarding

import androidx.lifecycle.viewModelScope
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.di.provideSoundPlayer
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.data.model.TokenModel
import com.dh.ondot.domain.di.SoundPlayer
import com.dh.ondot.domain.model.enums.AlarmMode
import com.dh.ondot.domain.model.enums.RingTone
import com.dh.ondot.domain.model.enums.SoundCategory
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.model.request.OnboardingRequest
import com.dh.ondot.domain.model.request.QuestionAnswer
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.repository.PlaceRepository
import com.dh.ondot.network.TokenProvider
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val placeRepository: PlaceRepository = ServiceLocator.placeRepository,
    private val memberRepository: MemberRepository = ServiceLocator.memberRepository,
    private val soundPlayer: SoundPlayer = provideSoundPlayer(),
    private val tokenProvider: TokenProvider = ServiceLocator.provideTokenProvider()
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
            4, 5 -> true
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
                soundPlayer.stopSound()
            }
            4 -> {
                updateState(uiState.value.copy(currentStep = 5))
            }
            5 -> {
                completeOnboarding()
            }
        }
    }

    // 뒤로가기 버튼을 클릭했을 때 호출되는 메서드
    fun onClickBack() {
        when (uiState.value.currentStep) {
            2 -> {
                updateState(uiState.value.copy(currentStep = 1))
            }
            3 -> {
                updateState(uiState.value.copy(currentStep = 2))
                soundPlayer.stopSound()
            }
            4 -> {
                updateState(uiState.value.copy(currentStep = 3))
            }
            5 -> {
                updateState(uiState.value.copy(currentStep = 4))
            }
            else -> {
                return
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
        soundPlayer.playSound(newSoundId)
    }

    fun onVolumeChange(newVolume: Float) {
        updateState(uiState.value.copy(volume = newVolume))
    }

    // ----------------------------------------- OnboardingStep4 ----------------------------

    fun onClickAnswer1(index: Int) {
        updateState(uiState.value.copy(selectedAnswer1Index = index))
    }

    // ----------------------------------------- OnboardingStep5 ----------------------------
    fun onClickAnswer2(index: Int) {
        updateState(uiState.value.copy(selectedAnswer2Index = index))
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            val soundCategory = when(uiState.value.selectedCategoryIndex) {
                0 -> SoundCategory.BRIGHT_ENERGY
                1 -> SoundCategory.FAST_INTENSE
                else -> { SoundCategory.BRIGHT_ENERGY }
            }

            val request = OnboardingRequest(
                preparationTime = uiState.value.preparationTime,
                roadAddress = uiState.value.selectedAddress?.roadAddress ?: "",
                longitude = uiState.value.selectedAddress?.longitude ?: 0.0,
                latitude = uiState.value.selectedAddress?.latitude ?: 0.0,
                alarmMode = AlarmMode.SOUND,
                isSnoozeEnabled = true,
                snoozeInterval = 1,
                snoozeCount = 3,
                soundCategory = soundCategory,
                ringTone = RingTone.getNameById(uiState.value.selectedSound ?: ""),
                volume = uiState.value.volume,
                questions = listOf(
                    QuestionAnswer(
                        questionId = 1,
                        answerId = uiState.value.answer1[uiState.value.selectedAnswer1Index].id
                    ),
                    QuestionAnswer(
                        questionId = 2,
                        answerId = uiState.value.answer2[uiState.value.selectedAnswer2Index].id
                    )
                )
            )

            memberRepository.completeOnboarding(request).collect {
                resultResponse(it, ::onSuccessCompleteOnboarding, ::onFailedCompleteOnboarding)
            }
        }
    }

    private fun onSuccessCompleteOnboarding(result: TokenModel) {
        viewModelScope.launch { tokenProvider.saveToken(result) }
    }

    private fun onFailedCompleteOnboarding(e: Throwable) {
        viewModelScope.launch {
            ToastManager.show(message = "온보딩 과정에서 에러가 발생했습니다.", ToastType.ERROR)
        }
    }
}