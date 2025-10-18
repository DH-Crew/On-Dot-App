package com.ondot.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.dh.ondot.data.local.db.OndotDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun provideDriverModule(): Module {
    return module {
        single<SqlDriver> { AndroidSqliteDriver(OndotDatabase.Schema, get(), "ondot.db") }
    }
}