package com.dh.ondot.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.domain.repository.AuthRepository
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.repository.PlaceRepository
import com.dh.ondot.presentation.setting.SettingViewModel
import kotlin.reflect.KClass

class SettingViewModelFactory(
    private val authRepository: AuthRepository = ServiceLocator.authRepository,
    private val memberRepository: MemberRepository = ServiceLocator.memberRepository,
    private val placeRepository: PlaceRepository = ServiceLocator.placeRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == SettingViewModel::class) {
            @Suppress("Unchecked_cast")
            return SettingViewModel(
                authRepository,
                memberRepository,
                placeRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.simpleName}")
    }
}