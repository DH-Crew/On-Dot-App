package com.ondot.domain.model.ui

import com.ondot.domain.model.enums.AlarmType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class AlarmEvent @OptIn(ExperimentalTime::class) constructor(
    val scheduleId: Long,
    val alarmId: Long,
    val type: AlarmType,
    val instanceId: Long = Clock.System.now().epochSeconds
) {
    /**
     * 이벤트에 유니크한 키값을 부여해, 알람을 구분할 수 있도록 한다. 중복 이벤트 필터링
     * */
    val key = Triple(scheduleId, alarmId, instanceId)
}
