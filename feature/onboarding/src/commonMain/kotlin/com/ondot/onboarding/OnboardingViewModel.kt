package com.ondot.onboarding

import androidx.lifecycle.viewModelScope
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.ondot.design_system.getPlatform
import com.ondot.domain.model.auth.AuthTokens
import com.ondot.domain.model.enums.AlarmMode
import com.ondot.domain.model.enums.RingTone
import com.ondot.domain.model.enums.SoundCategory
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.request.OnboardingRequest
import com.ondot.domain.model.request.QuestionAnswer
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.PlaceRepository
import com.ondot.domain.service.SoundPlayer
import com.ondot.domain.service.TokenProvider
import com.ondot.ui.base.BaseViewModel
import com.ondot.ui.util.ToastManager
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val placeRepository: PlaceRepository,
    private val memberRepository: MemberRepository,
    private val soundPlayer: SoundPlayer,
    private val tokenProvider: TokenProvider
): BaseViewModel<OnboardingUiState>(OnboardingUiState()) {

    // 온보딩 단계가 초기화되지 않은 경우 초기화하는 메서드
    fun initStep() {
        updateState(
            uiState.value.copy(
                currentStep = 1,
                totalStep = if (getPlatform() == ANDROID) 3 else 2
            )
        )
    }

    // 다음 버튼의 활성화 여부를 판단하는 메서드
    fun isButtonEnabled(): Boolean {
        if (getPlatform() == ANDROID) {
            return when (uiState.value.currentStep) {
                1 -> {
                    val hour = uiState.value.hourInput.toIntOrNull() ?: 0
                    val minute = uiState.value.minuteInput.toIntOrNull() ?: 0
                    hour > 0 || minute in 1..59
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
        } else {
            return when (uiState.value.currentStep) {
                1 -> {
                    val hour = uiState.value.hourInput.toIntOrNull() ?: 0
                    val minute = uiState.value.minuteInput.toIntOrNull() ?: 0
                    hour > 0 || minute in 1..59
                }
                2 -> {
                    uiState.value.selectedAddress != null
                }
                else -> {
                    false
                }
            }
        }
    }

    // 다음 버튼을 클릭했을 때 호출되는 메서드
    fun onClickNext() {
        if (getPlatform() == ANDROID) {
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
                    completeOnboarding()
                }
            }
        } else {
            when (uiState.value.currentStep) {
                1 -> {
                    updateState(uiState.value.copy(currentStep = 2))
                    calculatePreparationTime()
                }
                2 -> {
                    updateState(uiState.value.copy(currentStep = 3))
                    completeOnboarding()
                }
            }
        }
    }

    // 뒤로가기 버튼을 클릭했을 때 호출되는 메서드
    fun onClickBack() {
        if (getPlatform() == ANDROID) {
            when (uiState.value.currentStep) {
                2 -> {
                    updateState(uiState.value.copy(currentStep = 1))
                }
                3 -> {
                    updateState(uiState.value.copy(currentStep = 2))
                    soundPlayer.stopSound()
                }
                else -> {
                    return
                }
            }
        } else {
            when (uiState.value.currentStep) {
                2 -> {
                    updateState(uiState.value.copy(currentStep = 1))
                }
                else -> {
                    return
                }
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
        soundPlayer.stopSound()
        soundPlayer.playSound(newSoundId)
    }

    fun onVolumeChange(newVolume: Float) {
        updateState(uiState.value.copy(volume = newVolume))
        soundPlayer.setVolume(newVolume)
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            val soundCategory = when(uiState.value.selectedCategoryIndex) {
                0 -> SoundCategory.BRIGHT_ENERGY
                1 -> SoundCategory.FAST_INTENSE
                else -> SoundCategory.BRIGHT_ENERGY
            }

            val address = requireNotNull(uiState.value.selectedAddress) {
                "OnboardingViewModel: selectedAddress가 null인 상태에서 변환을 시도했습니다."
            }

            val request = OnboardingRequest(
                preparationTime = uiState.value.preparationTime,
                roadAddress = address.roadAddress,
                longitude = address.longitude,
                latitude = address.latitude,
                alarmMode = if (uiState.value.isMuted) AlarmMode.SILENT else AlarmMode.SOUND,
                isSnoozeEnabled = true,
                snoozeInterval = 1,
                snoozeCount = 3,
                soundCategory = soundCategory,
                ringTone = RingTone.getNameById(uiState.value.selectedSound ?: ""),
                volume = uiState.value.volume,
                questions = listOf(
                    QuestionAnswer(
                        questionId = 1,
                        answerId = uiState.value.answer1[0].id
                    ),
                    QuestionAnswer(
                        questionId = 2,
                        answerId = uiState.value.answer2[0].id
                    )
                )
            )

            memberRepository.completeOnboarding(request).collect {
                resultResponse(it, ::onSuccessCompleteOnboarding, ::onFailedCompleteOnboarding)
            }
        }
    }

    private fun onSuccessCompleteOnboarding(result: AuthTokens) {
        viewModelScope.launch { tokenProvider.saveToken(result) }
        emitEventFlow(OnboardingEvent.NavigateToMainScreen)
    }

    private fun onFailedCompleteOnboarding(e: Throwable) {
        viewModelScope.launch {
            ToastManager.show(message = "온보딩 과정에서 에러가 발생했습니다.", ToastType.ERROR)
        }
    }
}