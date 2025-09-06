package com.dh.ondot.data.local.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.dh.ondot.data.local.db.OndotDatabase
import com.dh.ondot.data.local.db.ScheduleEntityQueries
import com.dh.ondot.data.local.db.toDomain
import com.dh.ondot.domain.datasource.ScheduleLocalDataSource
import com.dh.ondot.domain.model.response.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ScheduleLocalDataSourceImpl(
    private val db: OndotDatabase
): ScheduleLocalDataSource {
    private val q = db.scheduleEntityQueries

    /**
     * 쿼리 결과 변환 과정
     * 1. getAll(): Query<ScheduleEntity>
     * 2. asFlow(): Flow<Query<ScheduleEntity>>
     * 3. mapToList(IO): Flow<List<ScheduleEntity>>
     * 4. map { it.toDomain }: Flow<List<Schedule>>
     * */
    override fun observeList(): Flow<List<Schedule>> =
        q.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }

    /**
     * mapToList, mapToOneOrNull 과 같은 코루틴 확장 함수는 내부적으로는 execute..() 형태의 blocking 메서드를 호출하지만
     * Flow 수집 시점에 blocking 쿼리 실행이 이루어지도록 보장해줌
     * 쉽게 말하자면 SQLDelight가 대신 withContext(Dispatchers.IO)를 처리해주는 형태
     * 파라미터로 넘겨주는 Dispatcher를 내부적으로 실행시켜줌
     * */
    override fun observeById(id: Long): Flow<Schedule?> =
        q.getById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity -> entity?.toDomain() }

    /**
     * executeAsOneOrNull() -> SQLDelight에서 단일 행을 즉시 가져오는 메서드
     * executeAsOneOrNull은 block api이기 때문에 호출한 스레드를 차단해버림. 메인 스레드에서 실행할 경우 ANR 발생 위험 높음
     * withContext(Dispatchers.IO) -> IO 스레드에서 실행. 메인 스레드를 차단하지 않음
     * */
    override suspend fun getByIdOnce(id: Long): Schedule? =
        withContext(Dispatchers.IO) {
            q.getById(id).executeAsOneOrNull()?.toDomain()
        }

    /**
     * transaction {} 내부는 Blocking API이므로 withContext(Dispatchers.IO)로 IO 스레드에서 실행
     * 반복문을 실행할 때 forEach는 inline lambda라서 I/O가 많아질 때 불필요한 람다 객체 생성을 유발할 수 있음
     * 단순 반복은 for 루프가 조금 더 효율적인 편
     * */
    override suspend fun upsertAll(items: List<Schedule>) = withContext(Dispatchers.IO) {
        db.transaction {
            for (item in items) {
                q.upsertSchedule(item)
            }
        }
    }

    override suspend fun upsert(item: Schedule) = withContext(Dispatchers.IO) {
        q.upsertSchedule(item)
    }

    private fun ScheduleEntityQueries.upsertSchedule(item: Schedule) {
        upsert(
            scheduleId = item.scheduleId,
            startLongitude = item.startLongitude,
            startLatitude = item.startLatitude,
            endLongitude = item.endLongitude,
            endLatitude = item.endLatitude,
            scheduleTitle = item.scheduleTitle,
            isRepeat = item.isRepeat,
            repeatDays = item.repeatDays,
            appointmentAt = item.appointmentAt,
            preparationAlarm = item.preparationAlarm,
            departureAlarm = item.departureAlarm,
            hasActiveAlarm = item.hasActiveAlarm
        )
    }

    override suspend fun deleteById(id: Long) {
        withContext(Dispatchers.IO) {
            q.deleteById(id)
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            q.clear()
        }
    }
}