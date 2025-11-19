package com.ondot.util

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object DateTimeFormatter {
    private const val WORD_AM = "오전"
    private const val WORD_PM = "오후"

    /**----------------------------------------------날짜---------------------------------------------*/

    data class YMD(val year: Int, val month: Int, val day: Int) {
        /** 두 자리 문자열 (5 → "05") */
        private fun Int.to2() = this.toString().padStart(2, '0')

        /** 원하는 구분자(delim)로 "yyyy{delim}MM{delim}DD" 포맷 */
        fun format(delim: String = "."): String =
            listOf(year, month.to2(), day.to2()).joinToString(delim)

        /** 06월 13일 화요일 형태로 포맷 */
        fun formatKoreanDate(): String {
            val date = LocalDate(year, month, day)
            val dayOfWeekKorean = when (date.dayOfWeek) {
                DayOfWeek.MONDAY    -> "월요일"
                DayOfWeek.TUESDAY   -> "화요일"
                DayOfWeek.WEDNESDAY -> "수요일"
                DayOfWeek.THURSDAY  -> "목요일"
                DayOfWeek.FRIDAY    -> "금요일"
                DayOfWeek.SATURDAY  -> "토요일"
                DayOfWeek.SUNDAY    -> "일요일"
                else -> ""
            }

            return "${month.pad2()}월 ${day.pad2()}일 $dayOfWeekKorean"
        }

        /** 06월 13일 형태로 포맷 */
        fun formatKoreanDateMonthDay(): String {
            val date = LocalDate(year, month, day)
            return "${month.pad2()}월 ${day.pad2()}일"
        }
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

    /** 06월 13일 화요일 형태로 포맷 */
    fun formatKoreanDate(iso: String): String = parseYMD(iso).formatKoreanDate()

    /** 06월 13일 형태로 포맷 */
    fun formatKoreanDateMonthDay(iso: String): String = parseYMD(iso).formatKoreanDateMonthDay()

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

        /** 23:05 같은 형태로 포맷 */
        fun formatHourMinute(): String {
            val hour24 = when {
                period == WORD_AM && hour12 == 12 -> 0
                period == WORD_AM -> hour12
                period == WORD_PM && hour12 == 12 -> 12
                else -> hour12 + 12
            }
            return "${hour24.pad2()}:${minute.pad2()}"
        }
    }

    data class HourMinuteSecond(
        val hour: Int,
        val minute: Int,
        val second: Int
    ) {
        private fun Int.pad2() = this.toString().padStart(2, '0')
        /** "01:00:00" 형태로 포맷 */
        fun format(): String = "${hour.pad2()}:${minute.pad2()}:${second.pad2()}"
    }

    private fun parseAmPmTime(iso: String): AmPmTime {
        val (h24, minute) = if ('T' in iso) {
            val dt = LocalDateTime.parse(iso)
            dt.hour to dt.minute
        } else {
            val t = LocalTime.parse(iso)
            t.hour to t.minute
        }

        val period = if (h24 < 12) WORD_AM else WORD_PM
        val hour12 = when (val m = h24 % 12) {
            0   -> 12
            else -> m
        }

        return AmPmTime(period, hour12, minute)
    }



    /** "오전 01:05" 같은 형태로 포맷 */
    fun formatAmPmTime(iso: String): String = parseAmPmTime(iso).format()

    /** "23:05" 같은 형태로 포맷 */
    fun formatHourMinute(iso: String): String = parseAmPmTime(iso).formatHourMinute()

    private fun parseHourMinuteSecond(iso: String): HourMinuteSecond {
        val localDt = LocalDateTime.parse(iso)
        val h24    = localDt.hour
        val minute = localDt.minute
        val second = localDt.second

        return HourMinuteSecond(h24, minute, second)
    }

    /** "01:00:00" 같은 형태로 포맷 */
    fun formatHourMinuteSecond(iso: String): String = parseHourMinuteSecond(iso).format()

    /** Pair("오전", "01:05") 같은 형태로 포맷 */
    fun formatAmPmTimePair(iso: String): Pair<String, String> {
        val (period, hour12, minute) = parseAmPmTime(iso)
        return period to "${hour12.toString().padStart(2,'0')}:${minute.toString().padStart(2,'0')}"
    }

    @OptIn(ExperimentalTime::class)
    fun calculateRemainingTime(iso: String): Triple<Int, Int, Int> {
        val localDt = LocalDateTime.parse(iso)
        val alarmInstant = localDt.toInstant(TimeZone.currentSystemDefault())

        val nowInstant: Instant = kotlin.time.Clock.System.now()
        val nowInSeoul: Instant = nowInstant
            .toLocalDateTime(TimeZone.of("Asia/Seoul"))
            .toInstant(TimeZone.of("Asia/Seoul"))

        val diff: Duration = alarmInstant - nowInSeoul
        if (diff.isNegative() || diff == Duration.ZERO) {
            return Triple(0, 0, 0)
        }
        val totalMinutes = diff.inWholeMinutes
        val days = (totalMinutes / (24 * 60)).toInt()
        val hours = ((totalMinutes % (24 * 60)) / 60).toInt()
        val minutes = (totalMinutes % 60).toInt()
        return Triple(days, hours, minutes)
    }

    /** 두 개의 ISO8601 문자열 간의 시간 차이를 계산 */
    @OptIn(ExperimentalTime::class)
    fun diffBetweenIsoTimes(iso1: String, iso2: String): Triple<Int, Int, Int> {
        return try {
            val dt1 = LocalDateTime.parse(iso1).toInstant(TimeZone.of("Asia/Seoul"))
            val dt2 = LocalDateTime.parse(iso2).toInstant(TimeZone.of("Asia/Seoul"))

            val diff: Duration = dt2 - dt1
            if (diff.isNegative() || diff == Duration.ZERO) {
                return Triple(0, 0, 0)
            }
            val totalSeconds = diff.inWholeSeconds

            val days = (totalSeconds / (24 * 3600)).toInt()
            val hours = ((totalSeconds % (24 * 3600)) / 3600).toInt()
            val minutes = ((totalSeconds % 3600) / 60).toInt()

            Triple(days, hours, minutes)
        } catch (e: Exception) {
            Triple(0, 0, 0)
        }
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

    /**----------------------------------------------ISO8601 변환---------------------------------------------*/

    /** Iso8601 기반의 문자열을 밀리초로 변환하는 메서드 */
    @OptIn(ExperimentalTime::class)
    fun isoStringToEpochMillis(iso: String): Long {
        // 타임존 없는 ISO 문자열은 "로컬 시간"으로 그대로 해석
        val localDt: LocalDateTime = LocalDateTime.parse(iso)
        // KST(Asia/Seoul) 기준 Instant로 변환
        val instant = localDt.toInstant(TimeZone.of("Asia/Seoul"))
        return instant.toEpochMilliseconds()
    }

    /** ISO-8601 문자열에서 날짜만 파싱 */
    fun String.toLocalDateFromIso(): LocalDate =
        LocalDate.parse(substringBefore('T'))

    /** ISO-8601 문자열에서 시간만 파싱 */
    fun String.toLocalTimeFromIso(): LocalTime =
        LocalTime.parse(substringAfter('T'))

    /** LocalTime에 interval(분) 만큼 더하기 */
    fun LocalTime.plusMinutes(interval: Int): LocalTime {
        // 현재 시각을 초 단위로 환산
        val totalSeconds = hour * 3600 + minute * 60 + second
        // interval 분을 초로 더함
        val addedSeconds = totalSeconds + interval * 60
        // 하루(86400초)로 모듈로 연산해서 wrap-around 처리
        val modSeconds = ((addedSeconds % 86400) + 86400) % 86400
        // 초를 시, 분, 초로 분해
        val newHour = (modSeconds / 3600).toInt()
        val newMinute = ((modSeconds % 3600) / 60).toInt()
        val newSecond = (modSeconds % 60).toInt()
        return LocalTime(newHour, newMinute, newSecond)
    }

    /**----------------------------------------------기타---------------------------------------------*/

    /** 두개의 ISO8601 날짜가 하루 차이인지 판단하는 메서드 */
    fun isYesterday(scheduleDate: String, alarmDate: String): Boolean {
        if (scheduleDate.isBlank() || alarmDate.isBlank()) return false

        return try {
            val scheduleLocalDate = scheduleDate.toLocalDateFromIso()
            val alarmLocalDate = alarmDate.toLocalDateFromIso()
            return scheduleLocalDate == alarmLocalDate.plus(DatePeriod(days = 1))
        } catch (_: Throwable) {
            false
        }
    }
}