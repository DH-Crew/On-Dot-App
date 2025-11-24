package com.ondot.login.di

import com.ondot.login.LoginViewModel
import com.ondot.login.navigation.LoginNavGraph
import com.ondot.navigation.base.NavGraphContributor
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val loginModule = module {
    viewModelOf(::LoginViewModel)
    single<NavGraphContributor> { LoginNavGraph }
}