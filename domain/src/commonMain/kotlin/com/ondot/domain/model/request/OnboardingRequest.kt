package com.dh.ondot.domain.model.request

import com.dh.ondot.domain.model.enums.AlarmMode
import com.dh.ondot.domain.model.enums.RingTone
import com.dh.ondot.domain.model.enums.SoundCategory
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingRequest(
    val preparationTime: Int,
    val roadAddress: String,
    val longitude: Double,
    val latitude: Double,
    val alarmMode: AlarmMode,
    val isSnoozeEnabled: Boolean,
    val snoozeInterval: Int,
    val snoozeCount: Int,
    val soundCategory: SoundCategory,
    val ringTone: RingTone,
    val volume: Float,
    val questions: List<QuestionAnswer>
)
