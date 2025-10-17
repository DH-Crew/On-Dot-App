package com.ondot.data.mapper

import com.ondot.data.model.response.schedule.ScheduleAlarmResponse
import com.ondot.domain.model.schedule.ScheduleAlarm
import com.ondot.network.base.Mapper

object ScheduleAlarmResponseMapper: Mapper<ScheduleAlarmResponse, ScheduleAlarm> {
    override fun responseToModel(response: ScheduleAlarmResponse?): ScheduleAlarm {
        return response?.let {
            ScheduleAlarm(
                preparationAlarm = AlarmResponseMapper.responseToModel(it.preparationAlarm),
                departureAlarm = AlarmResponseMapper.responseToModel(it.departureAlarm)
            )
        } ?: ScheduleAlarm()
    }
}