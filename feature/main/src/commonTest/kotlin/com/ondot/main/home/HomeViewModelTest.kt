package com.ondot.main.home

import com.ondot.domain.model.alarm.Alarm
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.domain.service.AlarmScheduler
import com.ondot.domain.service.AnalyticsManager
import com.ondot.domain.service.DirectionsOpener
import com.ondot.domain.service.LocalNotificationScheduler
import com.ondot.testing.fake.FakeMemberRepository
import com.ondot.testing.fake.FakeScheduleRepository
import com.ondot.testing.fake.util.FakeAlarmScheduler
import com.ondot.testing.fake.util.FakeAnalyticsManager
import com.ondot.testing.fake.util.FakeDirectionsOpener
import com.ondot.testing.fake.util.FakeNotificationScheduler
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

    private lateinit var memberRepository: MemberRepository
    private lateinit var scheduleRepository: ScheduleRepository
    private lateinit var directionsOpener: DirectionsOpener
    private lateinit var alarmScheduler: AlarmScheduler
    private lateinit var notificationScheduler: LocalNotificationScheduler
    private lateinit var analyticsManager: AnalyticsManager

    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        scheduleRepository = FakeScheduleRepository()
        memberRepository = FakeMemberRepository()
        directionsOpener = FakeDirectionsOpener()
        alarmScheduler = FakeAlarmScheduler()
        notificationScheduler = FakeNotificationScheduler()
        analyticsManager = FakeAnalyticsManager()

        viewModel = HomeViewModel(
            scheduleRepository = scheduleRepository,
            memberRepository = memberRepository,
            directionsOpener = directionsOpener,
            alarmScheduler = alarmScheduler,
            notificationScheduler = notificationScheduler,
            analyticsManager = analyticsManager
        )
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