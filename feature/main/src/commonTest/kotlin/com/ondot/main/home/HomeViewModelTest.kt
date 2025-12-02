package com.ondot.main.home

import com.ondot.domain.model.alarm.Alarm
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.testing.fake.FakeScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var scheduleRepository: ScheduleRepository

    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        scheduleRepository = FakeScheduleRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun dummySchedule(
        id: Long,
        hasPreparationNote: Boolean,
        enabled: Boolean
    ): Schedule {
        return Schedule(
            scheduleId = id,
            scheduleTitle = "테스트 스케줄 $id",
            appointmentAt = "2025-06-03T13:00:00",
            startLatitude = 37.0,
            startLongitude = 127.0,
            endLatitude = 37.5,
            endLongitude = 127.5,
            repeatDays = emptyList(),
            preparationNote = if (hasPreparationNote) "준비 메모" else "",
            hasActiveAlarm = enabled,
            departureAlarm = Alarm(
                alarmId = id * 10 + 1,
                enabled = enabled,
                triggeredAt = "2025-06-03T12:50:00"
            ),
            preparationAlarm = Alarm(
                alarmId = id * 10 + 2,
                enabled = enabled,
                triggeredAt = "2025-06-03T12:30:00"
            )
        )
    }
}