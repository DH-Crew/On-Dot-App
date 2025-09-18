package com.dh.ondot.core.di

import android.content.Context
import com.dh.ondot.data.OnDotDataStore

object AndroidServiceLocator {
    private lateinit var dataStore: OnDotDataStore

    fun init(
        context: Context
    ) {
        this.dataStore = OnDotDataStore(context)
    }

    fun provideDataStore() = dataStore
}