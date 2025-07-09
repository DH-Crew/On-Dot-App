package com.dh.ondot.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import kotlin.reflect.KClass

class GeneralScheduleViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T = GeneralScheduleViewModel() as T
}