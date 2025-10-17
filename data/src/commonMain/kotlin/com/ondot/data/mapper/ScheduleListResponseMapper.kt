package com.ondot.data.mapper

import com.ondot.data.model.response.schedule.ScheduleListResponse
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.schedule.ScheduleList
import com.ondot.network.base.Mapper

object ScheduleListResponseMapper: Mapper<ScheduleListResponse, ScheduleList> {
    override fun responseToModel(response: ScheduleListResponse?): ScheduleList {
        return response?.let {
            ScheduleList(
                earliestAlarmAt = it.earliestAlarmAt ?: "",
                hasNext = it.hasNext,
                scheduleList = it.scheduleList.map { scheduleResponse ->
                    Schedule(
                        scheduleId = scheduleResponse.scheduleId,
                        startLongitude = scheduleResponse.startLongitude,
                        startLatitude = scheduleResponse.startLatitude,
                        endLongitude = scheduleResponse.endLongitude,
                        endLatitude = scheduleResponse.endLatitude,
                        scheduleTitle = scheduleResponse.scheduleTitle,
                        isRepeat = scheduleResponse.isRepeat,
                        repeatDays = scheduleResponse.repeatDays,
                        appointmentAt = scheduleResponse.appointmentAt,
                        preparationAlarm = AlarmResponseMapper.responseToModel(scheduleResponse.preparationAlarm),
                        departureAlarm = AlarmResponseMapper.responseToModel(scheduleResponse.departureAlarm),
                        hasActiveAlarm = scheduleResponse.hasActiveAlarm
                    )
                }
            )
        } ?: ScheduleList()
    }
}