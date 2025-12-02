package com.ondot.testing.fake

import com.ondot.domain.model.alarm.Alarm
import com.ondot.domain.model.request.CreateScheduleRequest
import com.ondot.domain.model.request.ScheduleAlarmRequest
import com.ondot.domain.model.request.ToggleAlarmRequest
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.schedule.ScheduleAlarm
import com.ondot.domain.model.schedule.ScheduleDetail
import com.ondot.domain.model.schedule.ScheduleList
import com.ondot.domain.model.schedule.SchedulePreparation
import com.ondot.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeScheduleRepository: ScheduleRepository {

    /**
     * 인메모리 데이터베이스
     * */
    private val scheduleMap = mutableMapOf<Long, Schedule>()

    /**
     * 테스트 환경 제어 플래스 변수들
     * */
    var shouldFailNetwork: Boolean = false
    var lastToggleAlarmCalls = mutableListOf<Pair<Long, ToggleAlarmRequest>>()
    var lastDeleteCalls = mutableListOf<Long>()

    override suspend fun getScheduleList(): Flow<Result<ScheduleList>> = flow {
        if (shouldFailNetwork) {
            emit(Result.failure(Exception("네트워크 에러")))
        } else {
            val list = scheduleMap.values.toList()
            val scheduleList = ScheduleList(
                earliestAlarmAt = "",
                scheduleList = list
            )
            emit(Result.success(scheduleList))
        }
    }

    override suspend fun getScheduleAlarms(request: ScheduleAlarmRequest): Flow<Result<ScheduleAlarm>> = flow {
        emit(Result.success(ScheduleAlarm(preparationAlarm = Alarm(alarmId = 1), departureAlarm = Alarm(alarmId = 2))))
    }

    override suspend fun createSchedule(request: CreateScheduleRequest): Flow<Result<Unit>> = flow {
        // 인메모리 기준 아이디 1 증가
        val newId = (scheduleMap.keys.maxOrNull() ?: 0L) + 1L

        val schedule = Schedule(
            scheduleId = newId,
            scheduleTitle = request.title,
            appointmentAt = request.appointmentAt,
            repeatDays = request.repeatDays,
            preparationNote = request.preparationNote,
            hasActiveAlarm = false,
            departureAlarm = request.departureAlarm,
            preparationAlarm = request.preparationAlarm
        )

        scheduleMap[newId] = schedule

        emit(Result.success(Unit))
    }

    override suspend fun getScheduleDetail(scheduleId: Long): Flow<Result<ScheduleDetail>> = flow {
        val schedule = scheduleMap[scheduleId]

        if (schedule == null) {
            emit(Result.failure(NoSuchElementException("스케줄 없음 id: $scheduleId")))
        } else {
            val detail = ScheduleDetail(
                title = schedule.scheduleTitle,
                isRepeat = schedule.isRepeat,
                appointmentAt = schedule.appointmentAt,
                repeatDays = schedule.repeatDays,
                preparationAlarm = schedule.preparationAlarm,
                departureAlarm = schedule.departureAlarm
            )
            emit(Result.success(detail))
        }
    }

    override suspend fun deleteSchedule(scheduleId: Long): Flow<Result<Unit>> = flow {
        lastDeleteCalls += scheduleId
        scheduleMap.remove(scheduleId)
        emit(Result.success(Unit))
    }

    override suspend fun editSchedule(scheduleId: Long, request: ScheduleDetail): Flow<Result<Unit>> = flow {
        val existing = scheduleMap[scheduleId]
        if (existing == null) {
            emit(Result.failure(NoSuchElementException("스케줄 없음 id: $scheduleId")))
        } else {
            val updated = existing.copy(
                scheduleTitle = request.title,
                appointmentAt = request.appointmentAt,
                isRepeat = request.isRepeat,
                repeatDays = request.repeatDays,
                departureAlarm = request.departureAlarm,
                preparationAlarm = request.preparationAlarm
            )
            scheduleMap[scheduleId] = updated
            emit(Result.success(Unit))
        }
    }

    override suspend fun toggleAlarm(scheduleId: Long, request: ToggleAlarmRequest): Flow<Result<Unit>> = flow {
        lastToggleAlarmCalls += scheduleId to request
        val existing = scheduleMap[scheduleId]
        if (existing != null) {
            val updated = existing.copy(
                departureAlarm = existing.departureAlarm.copy(enabled = request.isEnabled),
                preparationAlarm = existing.preparationAlarm.copy(enabled = request.isEnabled)
            )
            scheduleMap[scheduleId] = updated
        }
        emit(Result.success(Unit))
    }

    override suspend fun getSchedulePreparationInfo(scheduleId: Long): Flow<Result<SchedulePreparation>> = flow {
        emit(Result.success(SchedulePreparation(preparationNote = "준비물")))
    }

    override suspend fun getLocalScheduleById(scheduleId: Long): Flow<Schedule?> = flow {
        emit(scheduleMap[scheduleId])
    }

    override suspend fun upsertLocalSchedule(schedule: Schedule) {
        scheduleMap[schedule.scheduleId] = schedule
    }

    /**
     * 테스트 유틸 메서드
     * */
    fun setInitialSchedules(list: List<Schedule>) {
        scheduleMap.clear()
        list.forEach { scheduleMap[it.scheduleId] = it }
    }

    fun getAllSchedules(): List<Schedule> = scheduleMap.values.toList()
}