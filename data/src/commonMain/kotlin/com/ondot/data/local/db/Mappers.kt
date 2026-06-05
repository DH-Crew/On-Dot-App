package com.ondot.data.local.db

import com.dh.ondot.data.local.db.Schedule_entity
import com.ondot.domain.model.enums.TransportType
import com.ondot.domain.model.schedule.Schedule

fun Schedule_entity.toDomain(): Schedule =
    Schedule(
        scheduleId = scheduleId,
        startLongitude = startLongitude,
        startLatitude = startLatitude,
        endLongitude = endLongitude,
        endLatitude = endLatitude,
        scheduleTitle = scheduleTitle,
        isRepeat = isRepeat,
        repeatDays = repeatDays,
        appointmentAt = appointmentAt,
        preparationAlarm = preparationAlarm,
        departureAlarm = departureAlarm,
        hasActiveAlarm = hasActiveAlarm,
        transportType = TransportType.from(transportType),
    )

fun Schedule.toEntity() =
    Schedule_entity(
        scheduleId = scheduleId,
        startLongitude = startLongitude,
        startLatitude = startLatitude,
        endLongitude = endLongitude,
        endLatitude = endLatitude,
        scheduleTitle = scheduleTitle,
        isRepeat = isRepeat,
        repeatDays = repeatDays,
        appointmentAt = appointmentAt,
        preparationAlarm = preparationAlarm,
        departureAlarm = departureAlarm,
        hasActiveAlarm = hasActiveAlarm,
        transportType = transportType.name,
    )
