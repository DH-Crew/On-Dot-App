package com.ondot.domain.model.command

import com.ondot.domain.model.enums.DayOfWeekKey
import com.ondot.domain.model.enums.TransportType
import com.ondot.domain.model.member.AddressInfo
import kotlinx.datetime.LocalTime

data class CreateEverytimeScheduleCommand(
    val selectedLectures: List<SelectedLecture>,
    val departurePlace: AddressInfo,
    val arrivalPlace: AddressInfo,
    val transportType: TransportType,
)

data class SelectedLecture(
    val day: DayOfWeekKey,
    val startTime: LocalTime,
)
