package com.dh.ondot.data.local.db

import app.cash.sqldelight.ColumnAdapter
import com.dh.ondot.domain.model.response.AlarmDetail
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// 기본값 포함
// 알 수 없는 필드 무시
private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

// Boolean <-> INTEGER(0/1)
object BooleanAsIntAdapter : ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long): Boolean = databaseValue != 0L
    override fun encode(value: Boolean): Long = if (value) 1L else 0L
}

// List<Int> <-> Text(Json)
object IntListAsJsonAdapter : ColumnAdapter<List<Int>, String> {
    private val serializer = ListSerializer(Int.serializer())

    override fun decode(databaseValue: String): List<Int> = json.decodeFromString(serializer, databaseValue)
    override fun encode(value: List<Int>): String = json.encodeToString(serializer, value)
}

// AlarmDetail <-> TEXT(JSON)
object AlarmDetailAsJsonAdapter : ColumnAdapter<AlarmDetail, String> {
    override fun decode(databaseValue: String): AlarmDetail = json.decodeFromString(databaseValue)
    override fun encode(value: AlarmDetail): String = json.encodeToString(value)
}