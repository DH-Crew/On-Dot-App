package com.dh.ondot.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.repository.PlaceRepository
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import kotlin.reflect.KClass

class GeneralScheduleViewModelFactory(
    private val scheduleRepository: ScheduleRepository = ServiceLocator.scheduleRepository,
    private val placeRepository: PlaceRepository = ServiceLocator.placeRepository,
    private val memberRepository: MemberRepository = ServiceLocator.memberRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extras: CreationExtras
    ): T {
        if (modelClass == GeneralScheduleViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return GeneralScheduleViewModel(
                scheduleRepository,
                placeRepository,
                memberRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.simpleName}")
    }
}