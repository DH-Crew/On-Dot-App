package com.ondot.everytime.contract

import com.dh.ondot.presentation.ui.theme.ERROR_EMPTY_URL
import com.dh.ondot.presentation.ui.theme.ERROR_SELECT_FIRST_CLASS
import com.dh.ondot.presentation.ui.theme.ERROR_VALIDATE_EVERYTIME_TIMETABLE
import com.dh.ondot.presentation.ui.theme.SUCCESS_VALIDATE_TIMETABLE
import com.ondot.domain.model.enums.DayOfWeekKey
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.schedule.EverytimeValidateTimetable
import com.ondot.domain.model.schedule.TimetableEntry
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.ui.base.mvi.BaseViewModel

class EverytimeViewModel(
    private val scheduleRepository: ScheduleRepository,
) : BaseViewModel<EverytimeUiState, EverytimeIntent, EverytimeSideEffect>(EverytimeUiState()) {
    override suspend fun handleIntent(intent: EverytimeIntent) {
        when (intent) {
            is EverytimeIntent.Validate -> validate(intent.url)
            EverytimeIntent.ClickNext -> clickNext()
            is EverytimeIntent.SelectClass -> selectClass(intent.item)
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
                reduce {
                    copy(
                        timetable = timetable,
                        selectedClassIdByDay = timetable.toDefaultSelectedClassIdByDay(),
                    )
                }
                emitEffect(EverytimeSideEffect.ShowToast(SUCCESS_VALIDATE_TIMETABLE, ToastType.INFO))
                emitEffect(EverytimeSideEffect.NavigateToTimetable)
            },
            onError = { error ->
                emitEffect(EverytimeSideEffect.ShowToast(ERROR_VALIDATE_EVERYTIME_TIMETABLE, ToastType.ERROR))
            },
        )
    }

    private fun selectClass(item: TimetableClassUiModel) {
        val currentSelectedId = currentState.selectedClassIdByDay[item.day]

        reduce {
            copy(
                selectedClassIdByDay =
                    if (currentSelectedId == item.id) {
                        selectedClassIdByDay - item.day
                    } else {
                        selectedClassIdByDay + (item.day to item.id)
                    },
            )
        }
    }

    private fun clickNext() {
        if (currentState.canGoNext.not()) {
            tryEmitEffect(
                EverytimeSideEffect.ShowToast(
                    ERROR_SELECT_FIRST_CLASS,
                    ToastType.ERROR,
                ),
            )
            return
        }

        tryEmitEffect(EverytimeSideEffect.NavigateToNext)
    }

    // 시간표 조회 이후에 가장 빠른 수업을 자동으로 선택하는 유틸 메서드
    private fun EverytimeValidateTimetable.toDefaultSelectedClassIdByDay(): Map<DayOfWeekKey, String> =
        timetable
            .mapNotNull { (day, entries) ->
                val firstClass =
                    entries.minByOrNull { entry ->
                        entry.startTime.hour * 60 + entry.startTime.minute
                    } ?: return@mapNotNull null

                day to firstClass.toClassId(day)
            }.toMap()

    private fun TimetableEntry.toClassId(day: DayOfWeekKey): String = "${day.name}_${courseName}_${startTime}_$endTime"
}
