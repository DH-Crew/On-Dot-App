package com.ondot.onboarding.contract

import com.dh.ondot.presentation.ui.theme.CATEGORY_BRIGHT_ENERGY
import com.dh.ondot.presentation.ui.theme.CATEGORY_FAST_INTENSE
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.RingTone
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.ui.AlarmSound
import com.ondot.ui.base.UiState

data class OnboardingUiState(
    val currentStep: Int = 0,
    val totalStep: Int = 0,
    // Step1
    val preparationTime: Int = 60,
    // Step2
    val addressInput: String = "",
    val placeList: List<AddressInfo> = emptyList(),
    val homeAddress: AddressInfo? = null,
    // Step3
    val isMuted: Boolean = false,
    val selectedCategoryIndex: Int = 0,
    val categories: List<String> = listOf(CATEGORY_BRIGHT_ENERGY, CATEGORY_FAST_INTENSE),
    val sounds: List<AlarmSound> =
        listOf(
            AlarmSound(RingTone.DANCING_IN_THE_STARDUST.id, "Dancing In The Stardust", CATEGORY_BRIGHT_ENERGY),
            AlarmSound(RingTone.IN_THE_CITY_LIGHTS_MIST.id, "In The City Lights Mist", CATEGORY_BRIGHT_ENERGY),
            AlarmSound(RingTone.FRACTURED_LOVE.id, "Fractured Love", CATEGORY_BRIGHT_ENERGY),
            AlarmSound(RingTone.CHASING_LIGHTS.id, "Chasing Lights", CATEGORY_BRIGHT_ENERGY),
            AlarmSound(RingTone.ASHES_OF_US.id, "Ashes of Us", CATEGORY_BRIGHT_ENERGY),
            AlarmSound(RingTone.HEATING_SUN.id, "Heating Sun", CATEGORY_BRIGHT_ENERGY),
            AlarmSound(RingTone.MEDAL.id, "Medal", CATEGORY_FAST_INTENSE),
            AlarmSound(RingTone.EXCITING_SPORTS_COMPETITIONS.id, "Exciting Sports Competitions", CATEGORY_FAST_INTENSE),
            AlarmSound(RingTone.POSITIVE_WAY.id, "Positive Way", CATEGORY_FAST_INTENSE),
            AlarmSound(RingTone.ENERGETIC_HAPPY_UPBEAT_ROCK_MUSIC.id, "Energetic Happy Upbeat Rock Music", CATEGORY_FAST_INTENSE),
            AlarmSound(RingTone.ENERGY_CATCHER.id, "Energy Catcher", CATEGORY_FAST_INTENSE),
        ),
    val filteredSounds: List<AlarmSound> = sounds.filter { it.category == categories[selectedCategoryIndex] },
    val selectedSound: String? = null,
    val volume: Float = 0.5f,
    // Step4
    val selectedMapProvider: MapProvider = MapProvider.NAVER,
) : UiState {
    val preparationTimeEnabled: Boolean
        get() = preparationTime > 0

    val addressInputEnabled: Boolean
        get() = homeAddress != null

    val alarmSoundEnabled: Boolean
        get() = isMuted || selectedSound != null
}
