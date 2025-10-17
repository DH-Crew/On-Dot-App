package com.ondot.data.mapper

import com.ondot.data.model.response.schedule.ScheduleDetailResponse
import com.ondot.domain.model.schedule.ScheduleDetail
import com.ondot.network.base.Mapper

object ScheduleDetailResponseMapper: Mapper<ScheduleDetailResponse, ScheduleDetail> {
    override fun responseToModel(response: ScheduleDetailResponse?): ScheduleDetail {
        return response?.let {
            ScheduleDetail(
                title = it.title,
                isRepeat = it.isRepeat,
                repeatDays = it.repeatDays,
                appointmentAt = it.appointmentAt,
                arrivalPlace = AddressResponseMapper.responseToModel(it.arrivalPlace),
                departurePlace = AddressResponseMapper.responseToModel(it.departurePlace),
                preparationAlarm = AlarmResponseMapper.responseToModel(it.preparationAlarm),
                departureAlarm = AlarmResponseMapper.responseToModel(it.departureAlarm),
            )
        } ?: ScheduleDetail()
    }
}