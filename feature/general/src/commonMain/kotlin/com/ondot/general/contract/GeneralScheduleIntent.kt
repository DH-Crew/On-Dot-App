package com.ondot.general.contract

import com.ondot.domain.model.enums.RouterType
import com.ondot.domain.model.enums.TimeType
import com.ondot.domain.model.enums.TransportType
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.PlaceHistory
import com.ondot.ui.base.mvi.Intent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed interface GeneralScheduleIntent : Intent {
    data object InitStep : GeneralScheduleIntent

    data class SetOnboardingEntry(
        val isFromOnboarding: Boolean,
    ) : GeneralScheduleIntent

    data class ToggleRepeat(
        val isRepeat: Boolean,
    ) : GeneralScheduleIntent

    data class SelectRepeatPreset(
        val index: Int,
    ) : GeneralScheduleIntent

    data class ToggleWeekDay(
        val index: Int,
    ) : GeneralScheduleIntent

    data object ToggleCalendar : GeneralScheduleIntent

    data object MoveToPreviousMonth : GeneralScheduleIntent

    data object MoveToNextMonth : GeneralScheduleIntent

    data class SelectDate(
        val date: LocalDate,
    ) : GeneralScheduleIntent

    data object ToggleTimeDial : GeneralScheduleIntent

    data class SelectTime(
        val time: LocalTime,
    ) : GeneralScheduleIntent

    data object InitHomeAddress : GeneralScheduleIntent

    data object InitPlaceHistory : GeneralScheduleIntent

    data class SetFocusedRouterType(
        val type: RouterType,
    ) : GeneralScheduleIntent

    data class UpdateRouteInput(
        val input: String,
    ) : GeneralScheduleIntent

    data class UpdateRouteInputByType(
        val type: RouterType,
        val input: String,
    ) : GeneralScheduleIntent

    data class SelectPlace(
        val place: AddressInfo,
    ) : GeneralScheduleIntent

    data class SelectHistory(
        val history: PlaceHistory,
    ) : GeneralScheduleIntent

    data class DeleteHistory(
        val history: PlaceHistory,
    ) : GeneralScheduleIntent

    data object ToggleHomeDeparture : GeneralScheduleIntent

    data class SetInitialPlacePicker(
        val isInitial: Boolean,
    ) : GeneralScheduleIntent

    data object ClickNext : GeneralScheduleIntent

    data object ClickBack : GeneralScheduleIntent

    data class UpdateScheduleTitle(
        val title: String,
    ) : GeneralScheduleIntent

    data object TogglePreparationAlarm : GeneralScheduleIntent

    data class SetBottomSheetVisible(
        val visible: Boolean,
    ) : GeneralScheduleIntent

    data class SetAlarmTimeBottomSheet(
        val type: TimeType?,
    ) : GeneralScheduleIntent

    data class UpdateAlarmTime(
        val type: TimeType,
        val date: LocalDate,
        val time: LocalTime,
    ) : GeneralScheduleIntent

    data class CreateSchedule(
        val isMedicationRequired: Boolean,
        val preparationNote: String,
    ) : GeneralScheduleIntent

    data class UpdateTransportType(
        val type: TransportType,
    ) : GeneralScheduleIntent
}
