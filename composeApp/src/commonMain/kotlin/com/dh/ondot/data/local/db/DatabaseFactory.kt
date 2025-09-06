package com.dh.ondot.data.local.db

import com.dh.ondot.core.platform.DriverFactory
import com.dh.ondot.presentation.ui.theme.DATABASE_NAME

class DatabaseFactory(private val driverFactory: DriverFactory) {
    fun create(): OndotDatabase {
        val driver = driverFactory.createDriver(DATABASE_NAME)

        return OndotDatabase(
            driver,
            Schedule_entity.Adapter(
                repeatDaysAdapter = IntListAsJsonAdapter,
                preparationAlarmAdapter = AlarmDetailAsJsonAdapter,
                departureAlarmAdapter = AlarmDetailAsJsonAdapter,
            )
        )
    }
}