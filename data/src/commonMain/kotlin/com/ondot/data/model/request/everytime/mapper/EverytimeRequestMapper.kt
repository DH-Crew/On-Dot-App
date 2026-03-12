package com.ondot.data.model.request.everytime.mapper

import com.ondot.data.model.request.everytime.CreateEverytimeScheduleRequest
import com.ondot.data.model.request.everytime.SelectedLectureRequest
import com.ondot.domain.model.command.CreateEverytimeScheduleCommand
import com.ondot.domain.model.command.SelectedLecture

fun CreateEverytimeScheduleCommand.toRequest(): CreateEverytimeScheduleRequest =
    CreateEverytimeScheduleRequest(
        selectedLectures = selectedLectures.map { it.toRequest() },
        departurePlace = departurePlace,
        arrivalPlace = arrivalPlace,
        transportType = transportType.name,
    )

fun SelectedLecture.toRequest(): SelectedLectureRequest =
    SelectedLectureRequest(
        day = day.name,
        startTime = startTime,
    )
