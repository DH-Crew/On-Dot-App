package com.dh.ondot.presentation.onboarding

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.core.util.SoundRes
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.model.ui.AlarmSound
import com.dh.ondot.domain.model.ui.OnboardingAnswer
import com.dh.ondot.presentation.ui.theme.CATEGORY_BRIGHT_ENERGY
import com.dh.ondot.presentation.ui.theme.CATEGORY_GENERAL

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
    val categories: List<String> = listOf(CATEGORY_GENERAL, CATEGORY_BRIGHT_ENERGY),
    val sounds: List<AlarmSound> = listOf(
        AlarmSound(SoundRes.DANCING, "Stardust", CATEGORY_BRIGHT_ENERGY),
        AlarmSound(SoundRes.RINGTONE, "Ringtone", CATEGORY_GENERAL),
    ),
    val filteredSounds: List<AlarmSound> = sounds.filter { it.category == categories[selectedCategoryIndex] },
    val selectedSound: String? = null,
    val volume: Float = 0.5f,
    // Step4
    val answer1: List<OnboardingAnswer> = listOf(
        OnboardingAnswer(1, "지각 방지"),
        OnboardingAnswer(2, "신경 쓰임 해소"),
        OnboardingAnswer(3, "간편한 일정 관리"),
        OnboardingAnswer(4, "정확한 출발 타이밍 알림"),
    ),
    val selectedAnswer1Index: Int = 0,
    // Step5
    val answer2: List<OnboardingAnswer> = listOf(
        OnboardingAnswer(5, "여유 있는 하루를 보내고 싶어서"),
        OnboardingAnswer(6, "중요한 사람과의 약속을 잘 지키고 싶어서"),
        OnboardingAnswer(7, "계획한 하루를 흐트러짐 없이 보내고 싶어서"),
        OnboardingAnswer(8, "지각 걱정 없이 신뢰받는 사람이 되고 싶어서"),
    ),
    val selectedAnswer2Index: Int = 0,

): UiState
