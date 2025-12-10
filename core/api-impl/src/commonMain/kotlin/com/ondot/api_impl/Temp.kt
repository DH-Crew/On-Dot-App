package com.ondot.api_impl

import com.ondot.api.AppNavEvent
import com.ondot.api.DeepLinkParser
import org.koin.dsl.module


/**
 * 지원 예시:
 * ondot://alarm?scheduleId=1&alarmId=2
 * ondot://today
 *
 * https://ondot.app/alarm?scheduleId=1&alarmId=2
 * https://ondot.app/today
 */
class DefaultDeepLinkParser : DeepLinkParser {

    override fun parse(url: String): AppNavEvent? {
        val normalized = url.trim()

        // 1) path 추출
        val path = extractPath(normalized) ?: return null

        // 2) query 추출
        val queryMap = extractQueryMap(normalized)

        return when (path) {
            "alarm" -> {
                val scheduleId = queryMap["scheduleId"]?.toLongOrNull()
                val alarmId = queryMap["alarmId"]?.toLongOrNull()
                AppNavEvent.OpenAlarm(scheduleId, alarmId)
            }
            "today" -> AppNavEvent.OpenToday
            "general" -> AppNavEvent.OpenGeneralSchedule
            else -> null
        }
    }

    private fun extractPath(url: String): String? {
        // scheme://host/path?query
        // scheme://path?query  (custom scheme 단순형)
        val noQuery = url.substringBefore("?")

        // 커스텀 스킴 케이스: ondot://alarm
        if (noQuery.contains("://")) {
            val afterScheme = noQuery.substringAfter("://")
            val parts = afterScheme.split("/")
                .filter { it.isNotBlank() }

            // cases:
            // ondot://alarm -> ["alarm"]
            // https://ondot.app/alarm -> ["ondot.app", "alarm"]
            return when {
                parts.isEmpty() -> null
                parts.size == 1 -> parts[0]
                else -> parts.last()
            }
        }

        // 이상 케이스
        return noQuery.split("/").lastOrNull()
    }

    private fun extractQueryMap(url: String): Map<String, String> {
        val query = url.substringAfter("?", missingDelimiterValue = "")
        if (query.isBlank()) return emptyMap()

        return query.split("&")
            .mapNotNull { pair ->
                val idx = pair.indexOf("=")
                if (idx <= 0) return@mapNotNull null
                val key = pair.substring(0, idx)
                val value = pair.substring(idx + 1)
                key to value
            }
            .toMap()
    }
}

val apiModule = module {
    single<DeepLinkParser> { DefaultDeepLinkParser() }
}