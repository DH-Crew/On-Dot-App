package com.dh.ondot.data.local.db

import com.dh.ondot.domain.model.response.Schedule

fun Schedule_entity.toDomain(): Schedule =
    Schedule(
        scheduleId       = scheduleId,
        startLongitude   = startLongitude,
        startLatitude    = startLatitude,
        endLongitude     = endLongitude,
        endLatitude      = endLatitude,
        scheduleTitle    = scheduleTitle,
        isRepeat         = isRepeat,
        repeatDays       = repeatDays,
        appointmentAt    = appointmentAt,
        preparationAlarm = preparationAlarm,
        departureAlarm   = departureAlarm,
        hasActiveAlarm   = hasActiveAlarm
    )

fun Schedule.toEntity() = Schedule_entity(
    scheduleId       = scheduleId,
    startLongitude   = startLongitude,
    startLatitude    = startLatitude,
    endLongitude     = endLongitude,
    endLatitude      = endLatitude,
    scheduleTitle    = scheduleTitle,
    isRepeat         = isRepeat,
    repeatDays       = repeatDays,
    appointmentAt    = appointmentAt,
    preparationAlarm = preparationAlarm,
    departureAlarm   = departureAlarm,
    hasActiveAlarm   = hasActiveAlarm
)