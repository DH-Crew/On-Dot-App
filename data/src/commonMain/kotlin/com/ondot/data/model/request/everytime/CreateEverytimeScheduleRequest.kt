package com.ondot.data.model.request.everytime

import com.ondot.domain.model.member.AddressInfo
import kotlinx.serialization.Serializable

@Serializable
data class CreateEverytimeScheduleRequest(
    val selectedLectures: List<SelectedLectureRequest>,
    val departurePlace: AddressInfo,
    val arrivalPlace: AddressInfo,
    val transportType: String,
)
