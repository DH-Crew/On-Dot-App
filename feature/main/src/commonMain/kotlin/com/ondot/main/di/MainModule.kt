package com.ondot.main.di

import com.ondot.main.MainViewModel
import com.ondot.main.home.HomeViewModel
import com.ondot.main.navigation.MainNavGraph
import com.ondot.main.setting.SettingViewModel
import com.ondot.main.setting.account_deletion.navigation.DeleteAccountNavGraph
import com.ondot.main.setting.home_address.navigation.HomeAddressNavGraph
import com.ondot.main.setting.nav_map.navigation.NavMapSettingNavGraph
import com.ondot.main.setting.preparation_time.navigation.PreparationTimeNavGraph
import com.ondot.main.setting.term.navigation.ServiceTermNavGraph
import com.ondot.navigation.base.NavGraphContributor
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingViewModel)
    single<NavGraphContributor> { MainNavGraph }
    single<NavGraphContributor> { DeleteAccountNavGraph }
    single<NavGraphContributor> { HomeAddressNavGraph }
    single<NavGraphContributor> { NavMapSettingNavGraph }
    single<NavGraphContributor> { PreparationTimeNavGraph }
    single<NavGraphContributor> { ServiceTermNavGraph }
}