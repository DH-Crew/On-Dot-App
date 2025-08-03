package com.dh.ondot.presentation.onboarding

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.enums.RingTone
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.model.ui.AlarmSound
import com.dh.ondot.domain.model.ui.UserAnswer
import com.dh.ondot.presentation.ui.theme.CATEGORY_BRIGHT_ENERGY
import com.dh.ondot.presentation.ui.theme.CATEGORY_FAST_INTENSE
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_ANSWER1
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_ANSWER2
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_ANSWER3
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_ANSWER4
import com.dh.ondot.presentation.ui.theme.ONBOARDING5_ANSWER1
import com.dh.ondot.presentation.ui.theme.ONBOARDING5_ANSWER2
import com.dh.ondot.presentation.ui.theme.ONBOARDING5_ANSWER3
import com.dh.ondot.presentation.ui.theme.ONBOARDING5_ANSWER4

data class OnboardingUiState(
    val currentStep: Int = 0,
    val totalStep: Int = 0,

    // Step1
    val preparationTime: Int = 0,
    val hourInput: String = "",
    val minuteInput: String = "",
    // Step2
    val addressInput: String = "",
    val roadAddress: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val addressList: List<AddressInfo> = emptyList(),
    val selectedAddress: AddressInfo? = null,
    // Step3
    val isMuted: Boolean = false,
    val selectedCategoryIndex: Int = 0,
    val categories: List<String> = listOf(CATEGORY_BRIGHT_ENERGY, CATEGORY_FAST_INTENSE),
    val sounds: List<AlarmSound> = listOf(
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
        AlarmSound(RingTone.ENERGY_CATCHER.id, "Energy Catcher", CATEGORY_FAST_INTENSE)
    ),
    val filteredSounds: List<AlarmSound> = sounds.filter { it.category == categories[selectedCategoryIndex] },
    val selectedSound: String? = null,
    val volume: Float = 0.5f,
    // Step4
    val answer1: List<UserAnswer> = listOf(
        UserAnswer(1, ONBOARDING4_ANSWER1),
        UserAnswer(2, ONBOARDING4_ANSWER2),
        UserAnswer(3, ONBOARDING4_ANSWER3),
        UserAnswer(4, ONBOARDING4_ANSWER4),
    ),
    val selectedAnswer1Index: Int = 0,
    // Step5
    val answer2: List<UserAnswer> = listOf(
        UserAnswer(5, ONBOARDING5_ANSWER1),
        UserAnswer(6, ONBOARDING5_ANSWER2),
        UserAnswer(7, ONBOARDING5_ANSWER3),
        UserAnswer(8, ONBOARDING5_ANSWER4),
    ),
    val selectedAnswer2Index: Int = 0,

): UiState
