package com.dh.ondot.domain.model.ui

import com.dh.ondot.domain.model.enums.AlarmType

data class AlarmEvent(
    val scheduleId: Long,
    val alarmId: Long,
    val type: AlarmType
) {
    /**
     * 이벤트에 유니크한 키값을 부여해, 알람을 구분할 수 있도록 한다. 중복 이벤트 필터링
     * */
    val key: String get() = "${scheduleId}_${alarmId}_${type}"
}
