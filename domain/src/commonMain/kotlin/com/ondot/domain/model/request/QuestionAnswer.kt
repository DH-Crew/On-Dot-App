package com.ondot.domain.model.request
import kotlinx.serialization.Serializable

@Serializable
data class QuestionAnswer(
    val questionId: Long,
    val answerId: Long
)
