package com.ondot.onboarding.contract

import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.Occupation
import com.ondot.domain.model.member.AddressInfo
import com.ondot.ui.base.mvi.Intent

sealed interface OnboardingIntent : Intent {
    data class InitStep(
        val currentStep: Int,
        val totalStep: Int,
    ) : OnboardingIntent

    data class SetPreparationTime(
        val preparationTime: Int,
    ) : OnboardingIntent

    data class SearchPlace(
        val query: String,
    ) : OnboardingIntent

    data class SetHomeAddress(
        val homeAddress: AddressInfo,
    ) : OnboardingIntent

    data class SetMute(
        val isMuted: Boolean,
    ) : OnboardingIntent

    data class SetSoundCategory(
        val categoryIndex: Int,
    ) : OnboardingIntent

    data class SetAlarmSound(
        val soundId: String,
    ) : OnboardingIntent

    data class SetVolume(
        val volume: Float,
    ) : OnboardingIntent

    data object StopSound : OnboardingIntent

    data class SetMapProvider(
        val mapProvider: MapProvider,
    ) : OnboardingIntent

    data class SetOccupation(
        val occupation: Occupation,
    ) : OnboardingIntent
}
