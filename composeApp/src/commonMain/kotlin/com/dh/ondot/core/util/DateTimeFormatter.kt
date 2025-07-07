package com.dh.ondot.core.util

object DateTimeFormatter {
    enum class Pattern(val format: String) {
        /** 년-월-일 (ex: 2025-05-10) */
        YMD("yyyy-MM-dd"),

        /** 년-월-일 시:분:초 (ex: 2025-05-10 18:30:00) */
        YMD_HMS("yyyy-MM-dd HH:mm:ss"),

        /** 월.일 (ex: 05.10) */
        MD("MM.dd")
    }

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

    fun formatDate(iso: String, delimiter: String = "."): String =
        parseYMD(iso).format(delimiter)
}