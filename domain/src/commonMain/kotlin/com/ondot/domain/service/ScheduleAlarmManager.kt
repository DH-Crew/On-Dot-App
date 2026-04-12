package com.ondot.domain.service

import com.ondot.domain.model.schedule.Schedule

/**
 * ViewModel에서 UI 데이터 기반으로 알람을 스케줄링, 취소하는 인터페이스
 * */
interface ScheduleAlarmManager {
    /**
     * 일괄 스케줄링
     * */
    suspend fun syncAll(
        schedules: List<Schedule>,
        previouslyScheduledAlarmIds: Set<Long> = emptySet(),
    ): Set<Long>

    /**
     * 일정 단건 스케줄링
     * */
    suspend fun schedule(schedule: Schedule)

    /**
     * 일정 단건 취소
     * */
    suspend fun cancel(schedule: Schedule)

    /**
     * 단건 토글
     * 토글을 클릭했을 때는 이 메서드를 호출
     * */
    suspend fun applyToggle(
        original: Schedule,
        enabled: Boolean,
    )

    /**
     * 단건 교체
     * 토글을 제외하고 내부 데이터 수정이나 준비 알람 on/off 시에 사용
     * */
    suspend fun replace(
        original: Schedule,
        updated: Schedule,
    ): Result<Unit>
}
