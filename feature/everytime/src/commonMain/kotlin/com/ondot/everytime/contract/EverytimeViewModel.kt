package com.ondot.everytime.contract

import com.dh.ondot.presentation.ui.theme.ERROR_EMPTY_URL
import com.dh.ondot.presentation.ui.theme.ERROR_VALIDATE_EVERYTIME_TIMETABLE
import com.dh.ondot.presentation.ui.theme.SUCCESS_VALIDATE_TIMETABLE
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.ui.base.mvi.BaseViewModel

class EverytimeViewModel(
    private val scheduleRepository: ScheduleRepository,
) : BaseViewModel<EverytimeUiState, EverytimeIntent, EverytimeSideEffect>(EverytimeUiState()) {
    override suspend fun handleIntent(intent: EverytimeIntent) {
        when (intent) {
            is EverytimeIntent.Validate -> validate(intent.url)
        }
    }

    private fun validate(url: String) {
        if (url.isBlank()) {
            tryEmitEffect(EverytimeSideEffect.ShowToast(ERROR_EMPTY_URL, ToastType.ERROR))
            return
        }

        launchResult(
            block = {
                scheduleRepository.validateEverytimeTimetable(
                    url = url,
                )
            },
            onSuccess = { timetable ->
                reduce { copy(timetable = timetable) }
                emitEffect(EverytimeSideEffect.ShowToast(SUCCESS_VALIDATE_TIMETABLE, ToastType.INFO))
            },
            onError = { error ->
                emitEffect(EverytimeSideEffect.ShowToast(ERROR_VALIDATE_EVERYTIME_TIMETABLE, ToastType.ERROR))
            },
        )
    }
}
