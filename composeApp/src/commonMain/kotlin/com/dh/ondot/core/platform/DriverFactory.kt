package com.dh.ondot.core.platform

import app.cash.sqldelight.db.SqlDriver
import com.dh.ondot.presentation.ui.theme.DATABASE_NAME

expect class DriverFactory {
    fun createDriver(dbName: String = DATABASE_NAME): SqlDriver
}