package com.ondot.general.di

import com.ondot.general.GeneralScheduleViewModel
import com.ondot.general.navigation.GeneralScheduleMviNavGraph
import com.ondot.general.navigation.GeneralScheduleNavGraph
import com.ondot.navigation.base.NavGraphContributor
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import com.ondot.general.contract.GeneralScheduleViewModel as GeneralScheduleMviViewModel

val generalModule =
    module {
        viewModelOf(::GeneralScheduleViewModel)
        viewModelOf(::GeneralScheduleMviViewModel)
        single<NavGraphContributor>(named("general")) { GeneralScheduleNavGraph }
        single<NavGraphContributor>(named("generalMvi")) { GeneralScheduleMviNavGraph }
    }
