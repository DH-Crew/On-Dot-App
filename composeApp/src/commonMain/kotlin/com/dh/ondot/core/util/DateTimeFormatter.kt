package com.dh.ondot.core.util

import com.dh.ondot.presentation.ui.theme.WORD_AM
import com.dh.ondot.presentation.ui.theme.WORD_PM
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object DateTimeFormatter {

    /**----------------------------------------------날짜---------------------------------------------*/

    data class YMD(val year: Int, val month: Int, val day: Int) {
        /** 두 자리 문자열 (5 → "05") */
        private fun Int.to2() = this.toString().padStart(2, '0')

        /** 원하는 구분자(delim)로 "yyyy{delim}MM{delim}DD" 포맷 */
        fun format(delim: String = "."): String =
            listOf(year, month.to2(), day.to2()).joinToString(delim)
    }

    private fun parseYMD(iso: String): YMD {
        require(iso.isNotBlank()) { "ISO 문자열은 공백이 아니어야 함" }

        val datePart = iso.substringBefore('T')
        val parts = datePart.split('-')

        require(parts.size >= 3) { "유효하지 않은 ISO 포맷: $iso" }

        val (y, m, d) = parts

        return YMD(
            year = y.toIntOrNull() ?: throw IllegalArgumentException("Invalid year in $iso"),
            month = m.toIntOrNull() ?: throw IllegalArgumentException("Invalid month in $iso"),
            day = d.toIntOrNull() ?: throw IllegalArgumentException("Invalid day in $iso")
        )
    }

    fun formatDate(iso: String, delimiter: String = "."): String = parseYMD(iso).format(delimiter)

    /** 주어진 연,월의 1일부터 마지막 일까지 리스트로 생성 */
    fun monthDays(year: Int, month: Int): List<LocalDate> {
        val first = LocalDate(year, month, 1)
        val last = first.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
        return (1..last.dayOfMonth).map { day -> LocalDate(year, month, day) }
    }

    fun LocalDate.formatKorean(): String {
        val mm = this.monthNumber.toString().padStart(2, '0')
        val dd = this.dayOfMonth.toString().padStart(2, '0')
        return "${this.year}년 ${mm}월 ${dd}일"
    }

    /**----------------------------------------------시간---------------------------------------------*/

    data class AmPmTime(
        val period: String,
        val hour12: Int,
        val minute: Int
    ) {
        private fun Int.pad2() = this.toString().padStart(2, '0')
        /** "오전 1:05" 같은 형태로 포맷 */
        fun format(): String = "$period $hour12:${minute.pad2()}"
    }

    private fun parseAmPmTime(iso: String): AmPmTime {
        require(iso.contains('T')) { "시간 정보가 포함된 ISO만 지원: $iso" }

        val timePart = iso
            .substringAfter('T')
            .substringBefore('Z')
            .substringBefore('+')
            .substringBefore('-')
        val parts = timePart.split(':')

        require(parts.size >= 2) { "유효하지 않은 시간 포맷: $iso" }

        val h24 = parts[0].toIntOrNull() ?: error("잘못된 시간(시): $iso")
        val minute = parts[1].toIntOrNull() ?: error("잘못된 시간(분): $iso")
        val period = if (h24 < 12) WORD_AM else WORD_PM
        val hour12 = when (val m = h24 % 12) {
            0 -> 12
            else -> m
        }

        return AmPmTime(period, hour12, minute)
    }

    /** "오전 01:05" 같은 형태로 포맷 */
    fun formatAmPmTime(iso: String): String = parseAmPmTime(iso).format()

    /** Pair("오전", "01:05") 같은 형태로 포맷 */
    fun formatAmPmTimePair(iso: String): Pair<String, String> {
        val (period, hour12, minute) = parseAmPmTime(iso)
        return period to "${hour12.toString().padStart(2,'0')}:${minute.toString().padStart(2,'0')}"
    }

    @OptIn(ExperimentalTime::class)
    fun calculateRemainingTime(iso: String): Triple<Int, Int, Int> {
        val normalized = if (iso.endsWith("Z") || iso.contains('+')) iso else iso + "Z"
        val alarmInstant = Instant.parse(normalized)
        val now = Clock.System.now()
        val diff: Duration = alarmInstant - now
        if (diff.isNegative() || diff == Duration.ZERO) {
            return Triple(0, 0, 0)
        }
        val totalMinutes = diff.inWholeMinutes
        val days = (totalMinutes / (24 * 60)).toInt()
        val hours = ((totalMinutes % (24 * 60)) / 60).toInt()
        val minutes = (totalMinutes % 60).toInt()
        return Triple(days, hours, minutes)
    }

    fun LocalTime.formatAmPmTime(): String {
        val period = if (hour < 12) WORD_AM else WORD_PM
        val hour12 = when (val h = hour % 12) {
            0 -> 12
            else -> h
        }

        val hh = hour12.toString().padStart(2, '0')
        val mm = minute.toString().padStart(2, '0')

        return "$period $hh:$mm"
    }

    /**----------------------------------------------ISO8601 생성---------------------------------------------*/

    private fun Int.pad2() = this.toString().padStart(2, '0')

    fun LocalDate.toIsoDateString(): String =
        "${year.toString().padStart(4, '0')}-" + "${monthNumber.pad2()}-" + dayOfMonth.pad2()

    fun LocalTime.toIsoTimeString(): String =
        "${hour.pad2()}:${minute.pad2()}:${second.pad2()}"

    fun formatIsoDateTime(date: LocalDate, time: LocalTime): String =
        "${date.toIsoDateString()}T${time.toIsoTimeString()}"
}