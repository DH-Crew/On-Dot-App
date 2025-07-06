package com.dh.ondot.presentation.onboarding

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.core.util.SoundRes
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.model.ui.AlarmSound
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
    val volume: Float = 0.5f

): UiState
