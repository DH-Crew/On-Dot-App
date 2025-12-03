package com.ondot.main.home

import co.touchlab.kermit.Logger
import com.ondot.domain.model.alarm.Alarm
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.repository.AlarmRepository
import com.ondot.testing.fake.FakeAlarmRepository
import com.ondot.testing.fake.FakeMemberRepository
import com.ondot.testing.fake.FakeScheduleRepository
import com.ondot.testing.fake.util.FakeAlarmScheduler
import com.ondot.testing.fake.util.FakeAnalyticsManager
import com.ondot.testing.fake.util.FakeDirectionsOpener
import com.ondot.testing.fake.util.FakeNotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val logger = Logger.withTag("HomeViewModelTest")

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var memberRepository: FakeMemberRepository
    private lateinit var scheduleRepository: FakeScheduleRepository
    private lateinit var directionsOpener: FakeDirectionsOpener
    private lateinit var alarmScheduler: FakeAlarmScheduler
    private lateinit var notificationScheduler: FakeNotificationScheduler
    private lateinit var analyticsManager: FakeAnalyticsManager

    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        startKoin {
            modules(
                module {
                    single<AlarmRepository> { FakeAlarmRepository() }
                }
            )
        }

        memberRepository = FakeMemberRepository()
        scheduleRepository = FakeScheduleRepository()
        directionsOpener = FakeDirectionsOpener()
        alarmScheduler = FakeAlarmScheduler()
        notificationScheduler = FakeNotificationScheduler()
        analyticsManager = FakeAnalyticsManager()

        createViewModel()
    }

    private fun createViewModel() {
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
        stopKoin()
        Dispatchers.resetMain()
    }

    /**---------------------------------------------needsChooseProvider----------------------------------------------*/

    @Test
    fun `needsChooseProvider 플래그 변경이 uiState에 반영된다`() = runTest {
        // 초기값은 true
        assertTrue(memberRepository.needsChooseProvider().first())

        // when: MapProvider 값 변경 → needsChooseProvider 플래그 변경
        viewModel.setMapProvider(MapProvider.NAVER)
        testDispatcher.scheduler.advanceUntilIdle()

        // then: HomeViewModel의 상태가 업데이트되어야 함
        assertFalse(viewModel.uiState.value.needsChooseProvider)
    }

    /**---------------------------------------------getScheduleList----------------------------------------------*/

    @Test
    fun `getScheduleList 성공시 스케줄 리스트와 알람 및 노티가 설정된다`() = runTest {
        // given
        val schedules = listOf(
            dummySchedule(id = 1L, hasPreparationNote = true, enabled = true),
            dummySchedule(id = 2L, hasPreparationNote = false, enabled = false)
        )
        scheduleRepository.setInitialSchedules(schedules)

        // when
        viewModel.getScheduleList()
        testDispatcher.scheduler.advanceUntilIdle()

        // then: uiState 업데이트 확인
        val state = viewModel.uiState.value
        assertEquals(2, state.scheduleList.size)
        assertEquals(setOf(1L, 2L), state.scheduleList.map { it.scheduleId }.toSet())
        // FakeScheduleRepository 는 earliestAlarmAt = "" 을 반환하므로 Triple(-1, -1, -1) 이어야 함
        assertEquals(Triple(-1, -1, -1), state.remainingTime)

        // then: 1번 스케줄: Preparation + Departure, 2번 스케줄: Departure 총 3개
        logger.e { "alarmScheduler.scheduled: ${alarmScheduler.scheduled}" }
        viewModel.uiState.value.scheduleList.forEach { schedule ->
            logger.e { "schedule: $schedule" }
        }
        assertEquals(3, alarmScheduler.scheduled.size)

        val scheduledInfos = alarmScheduler.scheduled.map { it.first }
        assertTrue(scheduledInfos.any { it.scheduleId == 1L && it.alarmType == AlarmType.Preparation })
        assertTrue(scheduledInfos.any { it.scheduleId == 1L && it.alarmType == AlarmType.Departure })
        assertTrue(scheduledInfos.any { it.scheduleId == 2L && it.alarmType == AlarmType.Departure })

        // 모든 알람은 기본 MapProvider.KAKAO 로 예약되어야 함
        assertTrue(alarmScheduler.scheduled.all { it.second == MapProvider.KAKAO })

        // then: 준비 메모 있는 스케줄만 노티
        assertEquals(1, notificationScheduler.scheduled.size)
        val notification = notificationScheduler.scheduled.first()
        assertEquals("1", notification.id)
        assertEquals("준비 메모", notification.body)
    }

    @Test
    fun `getScheduleList 실패시 스케줄 리스트와 사이드이펙트가 발생하지 않는다`() = runTest {
        // given
        scheduleRepository.shouldFailNetwork = true

        // when
        viewModel.getScheduleList()
        testDispatcher.scheduler.advanceUntilIdle()

        // then: uiState는 기본값(빈 리스트) 유지
        val state = viewModel.uiState.value
        assertTrue(state.scheduleList.isEmpty())

        // 알람/노티 스케줄링도 없어야 함
        assertTrue(alarmScheduler.scheduled.isEmpty())
        assertTrue(notificationScheduler.scheduled.isEmpty())
    }

    /**---------------------------------------------onToggle----------------------------------------------*/

    @Test
    fun `onToggle 호출 시 isExpanded 플래그가 토글된다`() = runTest {
        // given: 초기값은 false
        val initial = viewModel.uiState.value.isExpanded

        // when: 토글 이후 true
        viewModel.onToggle()
        val afterFirst = viewModel.uiState.value.isExpanded

        // when: 토글 이후 false
        viewModel.onToggle()
        val afterSecond = viewModel.uiState.value.isExpanded

        assertEquals(!initial, afterFirst)
        assertEquals(initial, afterSecond)
    }

    /**---------------------------------------------onClickAlarmSwitch (enable)----------------------------------------------*/

    @Test
    fun `알람 스위치를 켜면 UI 상태, Repo, 알람 스케줄, Analytics 가 모두 반영된다`() = runTest {
        // given
        val schedules = listOf(
            dummySchedule(id = 1L, hasPreparationNote = true, enabled = false),
            dummySchedule(id = 2L, hasPreparationNote = false, enabled = false)
        )
        scheduleRepository.setInitialSchedules(schedules)

        viewModel.getScheduleList()
        testDispatcher.scheduler.advanceUntilIdle()

        // 기존에 getScheduleList 에서 예약된 알람/노티는 초기화하고 시작
        alarmScheduler.clear()
        notificationScheduler.scheduled.clear()
        scheduleRepository.lastToggleAlarmCalls.clear()
        analyticsManager.events.clear()

        // when: 1번 스케줄 알람 ON
        viewModel.onClickAlarmSwitch(id = 1L, isEnabled = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // then: UI 상태 변경
        val state = viewModel.uiState.value
        val target = state.scheduleList.first { it.scheduleId == 1L }
        assertTrue(target.hasActiveAlarm)
        assertTrue(target.departureAlarm.enabled)
        assertTrue(target.preparationAlarm.enabled)

        // then: Repo.toggleAlarm 호출 확인
        assertEquals(1, scheduleRepository.lastToggleAlarmCalls.size)
        val (calledId, request) = scheduleRepository.lastToggleAlarmCalls.first()
        assertEquals(1L, calledId)
        assertTrue(request.isEnabled)

        // then: 알람 스케줄링 확인. 1번 스케줄: Preparation + Departure, 2번 스케줄: Departure
        assertEquals(3, alarmScheduler.scheduled.size)
        val scheduledInfos = alarmScheduler.scheduled.map { it.first }
        assertTrue(scheduledInfos.any { it.scheduleId == 1L && it.alarmType == AlarmType.Preparation })
        assertTrue(scheduledInfos.any { it.scheduleId == 1L && it.alarmType == AlarmType.Departure })
        assertTrue(scheduledInfos.any { it.scheduleId == 2L && it.alarmType == AlarmType.Departure })

        // then: Analytics 이벤트
        val lastEvent = analyticsManager.events.last()
        assertEquals("schedule_alarm_toggle", lastEvent.first)
        assertEquals(1L, lastEvent.second["schedule_id"])
        assertEquals(true, lastEvent.second["enabled"])
    }

    /**---------------------------------------------onClickAlarmSwitch (disable)----------------------------------------------*/

    @Test
    fun `알람 스위치를 끄면 UI 상태, Repo, 알람 취소, Analytics 가 모두 반영된다`() = runTest {
        // given: 처음부터 enabled = true 인 스케줄
        val schedules = listOf(
            dummySchedule(id = 1L, hasPreparationNote = true, enabled = true)
        )
        scheduleRepository.setInitialSchedules(schedules)

        viewModel.getScheduleList()
        testDispatcher.scheduler.advanceUntilIdle()

        alarmScheduler.clear()
        scheduleRepository.lastToggleAlarmCalls.clear()
        analyticsManager.events.clear()

        // when: 1번 스케줄 알람 OFF
        viewModel.onClickAlarmSwitch(id = 1L, isEnabled = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // then: UI 상태 확인
        val state = viewModel.uiState.value
        val target = state.scheduleList.first { it.scheduleId == 1L }
        assertFalse(target.hasActiveAlarm)
        assertFalse(target.departureAlarm.enabled)
        assertFalse(target.preparationAlarm.enabled)

        // then: Repo.toggleAlarm 호출 확인
        assertEquals(1, scheduleRepository.lastToggleAlarmCalls.size)
        val (calledId, request) = scheduleRepository.lastToggleAlarmCalls.first()
        assertEquals(1L, calledId)
        assertFalse(request.isEnabled)

        // then: 알람 취소 (두 개 다 취소)
        val expectedCancelled = listOf(1L * 10 + 1, 1L * 10 + 2)
        assertTrue(alarmScheduler.cancelledIds.containsAll(expectedCancelled))

        // then: Analytics
        val lastEvent = analyticsManager.events.last()
        assertEquals("alarms_cancelled_for_schedule", lastEvent.first)
        assertEquals(1L, lastEvent.second["schedule_id"])
    }

    /**---------------------------------------------deleteSchedule----------------------------------------------*/

    @Test
    fun `deleteSchedule 성공시 알람 취소, Repo 삭제, 노티 취소, GA, 상태 유지`() = runTest {
        // given
        val s1 = dummySchedule(id = 1L, hasPreparationNote = true, enabled = true)
        val s2 = dummySchedule(id = 2L, hasPreparationNote = false, enabled = false)
        scheduleRepository.setInitialSchedules(listOf(s1, s2))

        viewModel.getScheduleList()
        testDispatcher.scheduler.advanceUntilIdle()

        alarmScheduler.clear()
        notificationScheduler.scheduled.clear()
        notificationScheduler.cancelled.clear()
        scheduleRepository.lastDeleteCalls.clear()
        analyticsManager.events.clear()

        // when: 1번 스케줄 삭제 요청
        viewModel.deleteSchedule(scheduleId = 1L)

        // then: 즉시 알람 취소, UI 에서 제거
        val nowState = viewModel.uiState.value
        assertEquals(listOf(2L), nowState.scheduleList.map { it.scheduleId })

        val expectedCancelledIds = listOf(1L * 10 + 1, 1L * 10 + 2)
        assertTrue(alarmScheduler.cancelledIds.containsAll(expectedCancelledIds))

        // Repo.deleteSchedule는 delay(2000)으로 인해 아직 호출 전
        assertTrue(scheduleRepository.lastDeleteCalls.isEmpty())

        // GA 이벤트도 즉시 남아야 함
        val lastEvent = analyticsManager.events.last()
        assertEquals("schedule_delete_request", lastEvent.first)
        assertEquals(1L, lastEvent.second["schedule_id"])

        // when: 2초 흐른 뒤
        testDispatcher.scheduler.advanceTimeBy(2000)
        testDispatcher.scheduler.advanceUntilIdle()

        // then: Repo 삭제 호출 확인
        assertEquals(listOf(1L), scheduleRepository.lastDeleteCalls)

        // then: 노티 취소 호출
        assertTrue(notificationScheduler.cancelled.contains("1"))

        // then: 최종 상태에서도 1번 스케줄은 없어야 함
        val finalState = viewModel.uiState.value
        assertEquals(listOf(2L), finalState.scheduleList.map { it.scheduleId })
    }

    /**---------------------------------------------setMapProvider----------------------------------------------*/

    @Test
    fun `setMapProvider 성공시 needsChooseProvider false 로 변경되고 Repo flow 도 갱신된다`() = runTest {
        // given: 처음에 provider 선택이 필요하다고 가정
        memberRepository.setNeedsChooseProvider(true)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.needsChooseProvider)

        // when
        viewModel.setMapProvider(MapProvider.NAVER)
        testDispatcher.scheduler.advanceUntilIdle()

        // then: uiState 업데이트 확인
        assertFalse(viewModel.uiState.value.needsChooseProvider)

        // then: API 호출 기록 확인
        val lastRequest = memberRepository.lastMapProviderRequest
        assertEquals(MapProvider.NAVER, lastRequest?.mapProvider)

        // then: local map provider flow 도 NAVER 로 변경
        val provider = memberRepository.getLocalMapProvider().first()
        assertEquals(MapProvider.NAVER, provider)
    }

    @Test
    fun `setMapProvider 실패시 needsChooseProvider 는 그대로 유지되고 provider 도 바뀌지 않는다`() = runTest {
        // given
        memberRepository.setNeedsChooseProvider(true)
        testDispatcher.scheduler.advanceUntilIdle()
        memberRepository.shouldFailUpdateMapProvider = true

        val initialProvider = memberRepository.getLocalMapProvider().first()
        assertEquals(MapProvider.KAKAO, initialProvider)

        // when
        viewModel.setMapProvider(MapProvider.NAVER)
        testDispatcher.scheduler.advanceUntilIdle()

        // then: 여전히 provider 선택 필요 상태
        assertTrue(viewModel.uiState.value.needsChooseProvider)

        // then: local provider 는 그대로
        val provider = memberRepository.getLocalMapProvider().first()
        assertEquals(MapProvider.KAKAO, provider)
    }

    @Test
    fun `mapProvider 변경 이후 알람 스케줄링에 새로운 provider 가 사용된다`() = runTest {
        // given
        val schedules = listOf(
            dummySchedule(id = 1L, hasPreparationNote = true, enabled = true)
        )
        scheduleRepository.setInitialSchedules(schedules)

        // 기본 KAKAO 상태로 스케줄 리스트 로딩
        viewModel.getScheduleList()
        testDispatcher.scheduler.advanceUntilIdle()

        // KAKAO 로 예약됐는지 확인하고 초기화
        assertTrue(alarmScheduler.scheduled.all { it.second == MapProvider.KAKAO })
        alarmScheduler.clear()

        // when: 중간에 mapProvider를 NAVER 로 변경
        memberRepository.setInitialMapProvider(MapProvider.NAVER)
        testDispatcher.scheduler.advanceUntilIdle()

        // 다시 알람 스위치 ON → processAlarms(newList) 호출 트리거
        viewModel.onClickAlarmSwitch(id = 1L, isEnabled = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // then: 새로 예약된 알람은 NAVER로 되어 있어야 함
        assertTrue(alarmScheduler.scheduled.isNotEmpty())
        assertTrue(alarmScheduler.scheduled.all { it.second == MapProvider.NAVER })
    }

    /**---------------------------------------------openDirections----------------------------------------------*/

    @Test
    fun `openDirections는 현재 mapProvider와 스케줄 좌표를 사용해 DirectionsOpener를 호출한다`() = runTest {
        // given
        val s1 = dummySchedule(id = 1L, hasPreparationNote = true, enabled = true)
        scheduleRepository.setInitialSchedules(listOf(s1))

        // mapProvider 를 NAVER 로 세팅
        memberRepository.setInitialMapProvider(MapProvider.NAVER)

        viewModel.getScheduleList()
        testDispatcher.scheduler.advanceUntilIdle()

        // when: 경로 안내 보기
        viewModel.openDirections(id = 1L)

        // then: 호출 확인, 값 검증
        assertEquals(1, directionsOpener.calls.size)
        val call = directionsOpener.calls.first()

        assertEquals(s1.startLatitude, call.startLat)
        assertEquals(s1.startLongitude, call.startLng)
        assertEquals(s1.endLatitude, call.endLat)
        assertEquals(s1.endLongitude, call.endLng)
        assertEquals(MapProvider.NAVER, call.provider)
    }

    /**---------------------------------------------더미 데이터 생성 유틸 메서드----------------------------------------------*/

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
            hasActiveAlarm = true,
            departureAlarm = Alarm(
                alarmId = id * 10 + 1,
                enabled = true,
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