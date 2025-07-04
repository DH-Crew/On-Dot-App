package com.dh.ondot.core

import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.di.provideTokenProvider

fun initShared() {
    ServiceLocator.init(provideTokenProvider())
}