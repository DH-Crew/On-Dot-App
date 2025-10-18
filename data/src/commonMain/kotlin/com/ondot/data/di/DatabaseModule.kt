package com.ondot.data.di

import com.dh.ondot.data.local.db.OndotDatabase
import com.dh.ondot.data.local.db.Schedule_entity
import com.ondot.data.adapter.AlarmDetailAsJsonAdapter
import com.ondot.data.adapter.IntListAsJsonAdapter
import org.koin.dsl.module

val databaseModule = module {
    single<OndotDatabase> {
        OndotDatabase(
            get(),
            Schedule_entity.Adapter(
                repeatDaysAdapter = IntListAsJsonAdapter,
                preparationAlarmAdapter = AlarmDetailAsJsonAdapter,
                departureAlarmAdapter = AlarmDetailAsJsonAdapter,
            )
        )
    }
}
