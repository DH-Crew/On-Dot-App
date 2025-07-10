package com.dh.ondot.presentation.general

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.domain.model.enums.RouterType
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.model.response.HomeAddressInfo
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.repository.PlaceRepository
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.presentation.ui.theme.ERROR_GET_HOME_ADDRESS
import com.dh.ondot.presentation.ui.theme.ERROR_SEARCH_PLACE
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class GeneralScheduleViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val placeRepository: PlaceRepository,
    private val memberRepository: MemberRepository
) : BaseViewModel<GeneralScheduleUiState>(GeneralScheduleUiState()) {
    private val logger = Logger.withTag("GeneralScheduleViewModel")
    private val fullWeek = (0..6).toList()
    private val weekDays = (1..5).toList()
    private val weekend = listOf(0, 6)

    fun initStep() {
        updateState(uiState.value.copy(totalStep = 2, currentStep = 1))
    }

    fun initHomeAddress() {
        viewModelScope.launch {
            memberRepository.getHomeAddress().collect {
                resultResponse(it, ::onSuccessGetHomeAddress, ::onFailedGetHomeAddress)
            }
        }
    }

    private fun onSuccessGetHomeAddress(result: HomeAddressInfo) {
        updateState(
            uiState.value.copy(
                homeAddress = AddressInfo(
                    title = result.roadAddress,
                    roadAddress = result.roadAddress,
                    latitude = result.latitude,
                    longitude = result.longitude
                ),
                isHomeAddressInitialized = true
            )
        )
    }

    private fun onFailedGetHomeAddress(e: Throwable) {
        logger.e { "On Failed Get Home Address: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_HOME_ADDRESS, ToastType.ERROR) }
    }

    /**--------------------------------------------RepeatSettingSection-----------------------------------------------*/

    fun onClickSwitch(newValue: Boolean) {
        updateState(uiState.value.copy(isRepeat = newValue))

        if (!newValue) {
            updateState(
                uiState.value.copy(
                    activeCheckChip = null,
                    activeWeekDays = emptySet()
                )
            )
        }
    }

    fun onClickCheckTextChip(index: Int) {
        updateState(
            uiState.value.copy(
                activeCheckChip = index,
                isActiveCalendar = true,
                activeWeekDays = when (index) {
                    0 -> fullWeek.toSet()
                    1 -> weekDays.toSet()
                    2 -> weekend.toSet()
                    else -> emptySet()
                }
            )
        )
    }

    fun onClickTextChip(index: Int) {
        val activeWeekDays = uiState.value.activeWeekDays.toMutableSet()

        if (activeWeekDays.contains(index)) activeWeekDays.remove(index)
        else activeWeekDays.add(index)

        updateState(uiState.value.copy(
            isActiveCalendar = true,
            activeWeekDays = activeWeekDays,
            activeCheckChip = when (activeWeekDays) {
                fullWeek.toSet() -> 0
                weekDays.toSet()  -> 1
                weekend.toSet()  -> 2
                else -> null
            }
        ))
    }

    /**--------------------------------------------DateSettingSection-----------------------------------------------*/

    fun onToggleCalendar() {
        updateState(uiState.value.copy(isActiveCalendar = !uiState.value.isActiveCalendar))
    }

    fun onPrevMonth() {
        updateState(uiState.value.copy(calendarMonth = uiState.value.calendarMonth.minus(DatePeriod(months = 1))))
    }

    fun onNextMonth() {
        updateState(uiState.value.copy(calendarMonth = uiState.value.calendarMonth.plus(DatePeriod(months = 1))))
    }

    fun onSelectDate(date: LocalDate) {
        updateState(uiState.value.copy(selectedDate = date))
    }

    fun onToggleDial() {
        updateState(uiState.value.copy(isActiveDial = !uiState.value.isActiveDial))
    }

    fun onTimeSelected(newTime: LocalTime) {
        updateState(uiState.value.copy(selectedTime = newTime))
    }

    /**--------------------------------------------PlacePicker-----------------------------------------------*/

    fun onRouteInputFieldFocused(type: RouterType) {
        logger.d { "onRouteInputFieldFocused: $type" }
        updateStateSync(uiState.value.copy(lastFocusedTextField = type))
    }

    fun onRouteInputChanged(value: String) {
        onInputValueChanged(value)

        if (value.isBlank()) {
            updateState(uiState.value.copy(placeList = emptyList()))
            return
        }

        searchPlace(value)
    }

    private fun onInputValueChanged(value: String) {
        when (uiState.value.lastFocusedTextField) {
            RouterType.Departure -> updateState(uiState.value.copy(departurePlaceInput = value))
            RouterType.Arrival -> updateState(uiState.value.copy(arrivalPlaceInput = value))
        }
    }

    private fun searchPlace(query: String) {
        viewModelScope.launch {
            placeRepository.searchPlace(query).collect {
                resultResponse(it, ::onSuccessSearchPlace, ::onFailedSearchPlace)
            }
        }
    }

    private fun onSuccessSearchPlace(result: List<AddressInfo>) {
        updateState(uiState.value.copy(placeList = result))
    }

    private fun onFailedSearchPlace(e: Throwable) {
        logger.e { "On Failed Search Place: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_SEARCH_PLACE, ToastType.ERROR) }
    }

    fun onPlaceSelected(place: AddressInfo) {
        when (uiState.value.lastFocusedTextField) {
            RouterType.Departure -> updateState(
                uiState.value.copy(
                    selectedDeparturePlace = place,
                    placeList = emptyList(),
                    departurePlaceInput = place.title
                )
            )
            RouterType.Arrival -> updateState(
                uiState.value.copy(
                    selectedArrivalPlace = place,
                    placeList = emptyList(),
                    arrivalPlaceInput = place.title
                )
            )
        }
    }

    fun onClickCheckBox() {
        val curValue = uiState.value.isChecked

        updateState(
            uiState.value.copy(
                isChecked = !curValue,
                selectedDeparturePlace = if (!curValue) uiState.value.homeAddress else null,
                departurePlaceInput = if (!curValue) uiState.value.homeAddress.roadAddress else ""
            )
        )
    }

    /**--------------------------------------------ETC-----------------------------------------------*/

    fun isButtonEnabled(): Boolean {
        return when (uiState.value.currentStep) {
            1 -> uiState.value.selectedTime != null && (uiState.value.selectedDate != null || uiState.value.activeWeekDays.isNotEmpty())
            2 -> uiState.value.selectedDeparturePlace != null && uiState.value.selectedArrivalPlace != null
            else -> false
        }
    }

    fun onClickNextButton() {
        when (uiState.value.currentStep) {
            1 -> {
                updateState(uiState.value.copy(currentStep = uiState.value.currentStep + 1))
                emitEventFlow(GeneralScheduleEvent.NavigateToPlacePicker)
            }
            2 -> {
                emitEventFlow(GeneralScheduleEvent.NavigateToRouteLoading)
            }
        }

    }
}