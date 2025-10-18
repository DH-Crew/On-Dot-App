package com.ondot.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.dh.ondot.data.local.db.OndotDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun provideDriverModule(): Module {
    return module {
        single<SqlDriver> {
            NativeSqliteDriver(OndotDatabase.Schema, "ondot.db")
        }
    }
}