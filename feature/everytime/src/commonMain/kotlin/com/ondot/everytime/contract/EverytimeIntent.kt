package com.ondot.everytime.contract

import com.ondot.domain.model.enums.RouterType
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.PlaceHistory
import com.ondot.ui.base.mvi.Intent

sealed interface EverytimeIntent : Intent {
    data class Validate(
        val url: String,
    ) : EverytimeIntent

    data class SelectClass(
        val item: TimetableClassUiModel,
    ) : EverytimeIntent

    data object ValidateSelectedClass : EverytimeIntent

    data object CreateSchedule : EverytimeIntent

    data object ToggleCheckBox : EverytimeIntent

    data class SetSelectedPlace(
        val place: AddressInfo,
    ) : EverytimeIntent

    data class SetFocusedRouterType(
        val type: RouterType,
    ) : EverytimeIntent

    data class UpdateRouteInput(
        val input: String,
    ) : EverytimeIntent

    data class DeleteHistory(
        val history: PlaceHistory,
    ) : EverytimeIntent

    data class SelectHistory(
        val history: PlaceHistory,
    ) : EverytimeIntent

    data object InitPlaceHistory : EverytimeIntent
}
