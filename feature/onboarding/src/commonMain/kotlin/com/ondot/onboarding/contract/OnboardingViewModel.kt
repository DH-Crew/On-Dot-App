package com.ondot.onboarding.contract

import androidx.lifecycle.viewModelScope
import com.dh.ondot.presentation.ui.theme.ERROR_SEARCH_PLACE
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.PlaceRepository
import com.ondot.domain.service.SoundPlayer
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
                addressInput = homeAddress.roadAddress,
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
}
