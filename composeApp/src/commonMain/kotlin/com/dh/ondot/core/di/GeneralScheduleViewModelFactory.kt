package com.dh.ondot.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import kotlin.reflect.KClass

class GeneralScheduleViewModelFactory(
    private val scheduleRepository: ScheduleRepository = ServiceLocator.scheduleRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extras: CreationExtras
    ): T {
        if (modelClass == GeneralScheduleViewModel::class) {
            @Suppress("Unchecked_cast")
            return GeneralScheduleViewModel(scheduleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.simpleName}")
    }
}