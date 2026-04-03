package com.ondot.domain.model.enums

enum class ScheduleType {
    RECORD, // 과거
    ALARM, // 미래
    UNKNOWN,
    ;

    companion object {
        fun fromApi(type: String): ScheduleType =
            when (type) {
                "RECORD" -> RECORD
                "ALARM" -> ALARM
                else -> UNKNOWN
            }
    }
}
