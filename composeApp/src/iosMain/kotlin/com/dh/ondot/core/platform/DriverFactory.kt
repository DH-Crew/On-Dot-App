package com.dh.ondot.core.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.dh.ondot.data.local.db.OndotDatabase

actual class DriverFactory {
    actual fun createDriver(dbName: String): SqlDriver = NativeSqliteDriver(OndotDatabase.Schema, dbName)
}