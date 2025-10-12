package com.dh.ondot.domain.datasource

import com.dh.ondot.domain.model.response.Schedule
import kotlinx.coroutines.flow.Flow

interface ScheduleLocalDataSource {
    fun observeList(): Flow<List<Schedule>>                // 전체 조회
    fun observeById(id: Long): Flow<Schedule?>             // 단일 조회
    suspend fun getByIdOnce(id: Long): Schedule?           // 단발 조회
    suspend fun upsertAll(items: List<Schedule>)
    suspend fun upsert(item: Schedule)
    suspend fun deleteById(id: Long)
    suspend fun clear()
}