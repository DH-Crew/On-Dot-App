package com.ondot.onboarding.contract

import androidx.lifecycle.viewModelScope
import com.dh.ondot.presentation.ui.theme.ERROR_COMPLETE_ONBOARDING
import com.dh.ondot.presentation.ui.theme.ERROR_SEARCH_PLACE
import com.ondot.domain.model.enums.AlarmMode
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.Occupation
import com.ondot.domain.model.enums.RingTone
import com.ondot.domain.model.enums.SoundCategory
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.request.OnboardingRequest
import com.ondot.domain.model.request.QuestionAnswer
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.PlaceRepository
import com.ondot.domain.service.MapProviderStorage
import com.ondot.domain.service.SoundPlayer
import com.ondot.domain.service.TokenProvider
import com.ondot.ui.base.mvi.BaseViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class OnboardingViewModel(
    private val memberRepository: MemberRepository,
    private val placeRepository: PlaceRepository,
    private val soundPlayer: SoundPlayer,
    private val tokenProvider: TokenProvider,
    private val mapProviderStorage: MapProviderStorage,
) : BaseViewModel<OnboardingUiState, OnboardingIntent, OnboardingSideEffect>(OnboardingUiState()) {
    private val query = MutableStateFlow("")
    private var searchPlaceJob: Job? = null

    init {
        viewModelScope.launch {
            query
                .debounce(100)
                .distinctUntilChanged()
                .onEach { value ->
                    if (value.isBlank()) {
                        searchPlaceJob?.cancel()
                        reduce {
                            copy(
                                placeList = emptyList(),
                            )
                        }
                    }
                }.filter { it.isNotBlank() }
                .collect { q ->
                    searchPlace(q)
                }
        }
    }

    override suspend fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.InitStep -> {
                setCurrentStep(intent.currentStep)
                setTotalStep(intent.totalStep)
            }
            is OnboardingIntent.SetPreparationTime -> setPreparationTime(intent.preparationTime)
            is OnboardingIntent.SearchPlace -> updateAddressInput(intent.query)
            is OnboardingIntent.SetHomeAddress -> setHomeAddress(intent.homeAddress)
            is OnboardingIntent.SetMute -> setMute(intent.isMuted)
            is OnboardingIntent.SetSoundCategory -> setSoundCategory(intent.categoryIndex)
            is OnboardingIntent.SetAlarmSound -> setAlarmSound(intent.soundId)
            is OnboardingIntent.SetVolume -> setVolume(intent.volume)
            is OnboardingIntent.StopSound -> stopSound()
            is OnboardingIntent.SetMapProvider -> setMapProvider(intent.mapProvider)
            is OnboardingIntent.SetOccupation -> setOccupation(intent.occupation)
        }
    }

    private fun setCurrentStep(currentStep: Int) {
        reduce { copy(currentStep = currentStep) }
    }

    private fun setTotalStep(totalStep: Int) {
        reduce { copy(totalStep = totalStep) }
    }

    private fun setPreparationTime(preparationTime: Int) {
        reduce { copy(preparationTime = preparationTime) }
    }

    private fun updateAddressInput(value: String) {
        reduce {
            copy(
                addressInput = value,
                homeAddress = null,
            )
        }
        query.value = value
    }

    private fun setHomeAddress(homeAddress: AddressInfo) {
        reduce {
            copy(
                homeAddress = homeAddress,
                addressInput = homeAddress.title,
                placeList = emptyList(),
            )
        }
    }

    private fun setMute(isMuted: Boolean) {
        reduce { copy(isMuted = isMuted) }
        if (isMuted) {
            soundPlayer.stopSound()
        }
    }

    private fun setSoundCategory(categoryIndex: Int) {
        reduce {
            val category = categories.getOrNull(categoryIndex) ?: return@reduce this

            copy(
                selectedCategoryIndex = categoryIndex,
                filteredSounds = sounds.filter { it.category == category },
            )
        }
    }

    private fun setAlarmSound(soundId: String) {
        reduce { copy(selectedSound = soundId) }
        soundPlayer.stopSound()
        soundPlayer.playSound(soundId)
    }

    private fun setVolume(volume: Float) {
        reduce { copy(volume = volume) }
        soundPlayer.setVolume(volume)
    }

    private fun stopSound() {
        soundPlayer.stopSound()
    }

    private fun setMapProvider(mapProvider: MapProvider) {
        reduce { copy(selectedMapProvider = mapProvider) }
    }

    private fun searchPlace(query: String) {
        searchPlaceJob?.cancel()
        searchPlaceJob =
            launchResult(
                block = { placeRepository.searchPlaceAppResult(query) },
                onSuccess = { places ->
                    reduce {
                        copy(
                            placeList = places,
                        )
                    }
                },
                onError = {
                    emitEffect(OnboardingSideEffect.ShowToast(ERROR_SEARCH_PLACE, ToastType.ERROR))
                },
            )
    }

    private fun setOccupation(occupation: Occupation) {
        if (currentState.isSubmitting) return

        reduce { copy(selectedOccupation = occupation) }
        completeOnboarding()
    }

    private fun completeOnboarding() {
        val soundCategory =
            when (currentState.selectedCategoryIndex) {
                0 -> SoundCategory.BRIGHT_ENERGY
                1 -> SoundCategory.FAST_INTENSE
                else -> SoundCategory.BRIGHT_ENERGY
            }

        val request =
            OnboardingRequest(
                preparationTime = currentState.preparationTime,
                roadAddress = currentState.homeAddress?.roadAddress ?: "",
                longitude = currentState.homeAddress?.longitude ?: 0.0,
                latitude = currentState.homeAddress?.latitude ?: 0.0,
                alarmMode = if (currentState.isMuted) AlarmMode.SILENT else AlarmMode.SOUND,
                isSnoozeEnabled = true,
                snoozeInterval = 1,
                snoozeCount = 3,
                soundCategory = soundCategory,
                ringTone = RingTone.getNameById(currentState.selectedSound ?: ""),
                volume = currentState.volume,
                questions =
                    listOf(
                        QuestionAnswer(
                            questionId = 1,
                            answerId = 1,
                        ),
                        QuestionAnswer(
                            questionId = 2,
                            answerId = 5,
                        ),
                    ),
                mapProvider = currentState.selectedMapProvider,
                occupation = currentState.selectedOccupation,
            )

        launchResult(
            block = { memberRepository.completeOnboardingMvi(request) },
            onStart = {
                reduce { copy(isSubmitting = true) }
            },
            onFinally = {
                reduce { copy(isSubmitting = false) }
            },
            onSuccess = {
                tokenProvider.saveToken(it)
                mapProviderStorage.setMapProvider(currentState.selectedMapProvider)
                when (currentState.selectedOccupation) {
                    Occupation.OFFICE_WORKER -> emitEffect(OnboardingSideEffect.NavigateToGeneralSchedule)
                    Occupation.UNIVERSITY_STUDENT -> emitEffect(OnboardingSideEffect.NavigateToEverytime)
                    Occupation.ETC -> emitEffect(OnboardingSideEffect.NavigateToMainScreen)
                }
            },
            onError = {
                emitEffect(OnboardingSideEffect.ShowToast(ERROR_COMPLETE_ONBOARDING, ToastType.ERROR))
            },
        )
    }
}
