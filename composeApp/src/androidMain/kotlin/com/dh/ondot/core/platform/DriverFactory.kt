package com.dh.ondot.core.platform

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.dh.ondot.data.local.db.OndotDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(dbName: String): SqlDriver = AndroidSqliteDriver(OndotDatabase.Schema, context, dbName)
}