package com.ondot.general

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.presentation.ui.theme.ERROR_CREATE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_GET_HOME_ADDRESS
import com.dh.ondot.presentation.ui.theme.ERROR_GET_PLACE_HISTORY
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_ALARMS
import com.dh.ondot.presentation.ui.theme.ERROR_SEARCH_PLACE
import com.ondot.domain.model.enums.RouterType
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.request.CreateScheduleRequest
import com.ondot.domain.model.request.ScheduleAlarmRequest
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.HomeAddressInfo
import com.ondot.domain.model.member.PlaceHistory
import com.ondot.domain.model.request.DeletePlaceHistoryRequest
import com.ondot.domain.model.schedule.ScheduleAlarm
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.PlaceRepository
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.domain.service.AnalyticsManager
import com.ondot.ui.base.BaseViewModel
import com.ondot.ui.util.ToastManager
import com.ondot.util.DateTimeFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class GeneralScheduleViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val placeRepository: PlaceRepository,
    private val memberRepository: MemberRepository,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel<GeneralScheduleUiState>(GeneralScheduleUiState()) {
    private val logger = Logger.withTag("GeneralScheduleViewModel")
    private val fullWeek = (0..6).toList()
    private val weekDays = (1..5).toList()
    private val weekend = listOf(0, 6)
    private val _query = MutableStateFlow("")
    private val query: StateFlow<String> = _query

    init {
        viewModelScope.launch {
            query
                .debounce(100)
                .distinctUntilChanged()
                .onEach { value ->
                    if (value.isBlank()) {
                        updateState(uiState.value.copy(placeList = emptyList()))
                    }
                }
                .filter { it.isNotBlank() }
                .flatMapLatest { q ->
                    placeRepository.searchPlace(q)
                }
                .collect { response ->
                    resultResponse(
                        response,
                        ::onSuccessSearchPlace,
                        ::onFailedSearchPlace
                    )
                }
        }
    }

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
        updateState(uiState.value.copy(isRepeat = newValue, selectedDate = null))

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

        val bucket = when (index) { 0 -> "full_week"; 1 -> "weekdays"; 2 -> "weekend"; else -> "custom" }
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
        val target = uiState.value.calendarMonth.minus(DatePeriod(months = 1))
        updateState(uiState.value.copy(calendarMonth = target))
    }

    fun onNextMonth() {
        val target = uiState.value.calendarMonth.plus(DatePeriod(months = 1))
        updateState(uiState.value.copy(calendarMonth = target))
    }

    fun onSelectDate(date: LocalDate) {
        updateState(uiState.value.copy(selectedDate = date, isActiveDial = true))
    }

    fun onToggleDial() {
        updateState(uiState.value.copy(isActiveDial = !uiState.value.isActiveDial))
    }

    fun onTimeSelected(newTime: LocalTime) {
        updateState(uiState.value.copy(selectedTime = newTime))
    }

    /**--------------------------------------------PlacePicker-----------------------------------------------*/

    fun onRouteInputFieldFocused(type: RouterType) {
        updateStateSync(uiState.value.copy(lastFocusedTextField = type))
    }

    fun onRouteInputChanged(value: String) {
        onInputValueChanged(value)
        _query.value = value
    }

    private fun onInputValueChanged(value: String) {
        when (uiState.value.lastFocusedTextField) {
            RouterType.Departure -> updateState(uiState.value.copy(departurePlaceInput = value))
            RouterType.Arrival -> updateState(uiState.value.copy(arrivalPlaceInput = value))
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
        savePlaceHistory(place)

        when (uiState.value.lastFocusedTextField) {
            RouterType.Departure -> {
                updateState(
                    uiState.value.copy(
                        selectedDeparturePlace = place,
                        placeList = emptyList(),
                        departurePlaceInput = place.title
                    )
                )
                emitEventFlow(GeneralScheduleEvent.RequestArrivalFocus)
                onRouteInputFieldFocused(RouterType.Arrival)
                _query.value = ""
            }
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

        if (!curValue && uiState.value.homeAddress.title.isBlank()) {
            initHomeAddress()
            return
        }

        updateState(
            uiState.value.copy(
                isChecked = !curValue,
                selectedDeparturePlace = if (!curValue) uiState.value.homeAddress else null,
                departurePlaceInput = if (!curValue) uiState.value.homeAddress.roadAddress else ""
            )
        )
    }

    fun updateInitialPlacePicker(value: Boolean) {
        updateState(uiState.value.copy(isInitialPlacePicker = value))
    }

    fun getPlaceHistory() {
        viewModelScope.launch {
            placeRepository.getPlaceHistory().collect {
                resultResponse(it, ::onSuccessGetPlaceHistory, ::onFailedGetPlaceHistory)
            }
        }
    }

    private fun onSuccessGetPlaceHistory(result: List<PlaceHistory>) {
        updateState(uiState.value.copy(placeHistory = result))
    }

    private fun onFailedGetPlaceHistory(e: Throwable) {
        logger.e { "On Failed Get Place History: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_PLACE_HISTORY, ToastType.ERROR) }
    }

    private fun savePlaceHistory(place: AddressInfo) {
        viewModelScope.launch {
            placeRepository.savePlaceHistory(place).collect {
                resultResponse(it, ::onSuccessSavePlaceHistory)
            }
        }
    }

    private fun onSuccessSavePlaceHistory(result: Unit) {
        getPlaceHistory()
    }

    fun onHistoryPlaceSelected(place: PlaceHistory) {
        val address = AddressInfo(
            title = place.title,
            roadAddress = place.roadAddress,
            latitude = place.latitude,
            longitude = place.longitude
        )

        onPlaceSelected(address)
    }

    fun deletePlaceHistory(place: PlaceHistory) {
        viewModelScope.launch {
            placeRepository.deletePlaceHistory(request = DeletePlaceHistoryRequest(place.searchedAt)).collect {
                resultResponse(it, ::onSuccessDeletePlaceHistory)
            }
        }
    }

    private fun onSuccessDeletePlaceHistory(result: Unit) {
        getPlaceHistory()
    }

    /**--------------------------------------------RouteLoading-----------------------------------------------*/

    @OptIn(ExperimentalTime::class)
    private fun getScheduleAlarms() {
        val (dateRepeat, time, places) = validateScheduleInputs()
        val (date, repeatDays) = dateRepeat
        val (departurePlace, arrivalPlace) = places

        val zone = TimeZone.currentSystemDefault()
        val today = kotlin.time.Clock.System.now().toLocalDateTime(zone).date
        val dateOrToday = date ?: today

        val appointmentAt = DateTimeFormatter.formatIsoDateTime(dateOrToday, time)

        viewModelScope.launch {
            scheduleRepository.getScheduleAlarms(
                request = ScheduleAlarmRequest(
                    appointmentAt = appointmentAt,
                    startLatitude = departurePlace.latitude,
                    startLongitude = departurePlace.longitude,
                    endLatitude = arrivalPlace.latitude,
                    endLongitude = arrivalPlace.longitude
                )
            ).collect {
                resultResponse(it, ::onSuccessGetScheduleAlarms, ::onFailedGetScheduleAlarms)
            }
        }
    }

    private fun onSuccessGetScheduleAlarms(result: ScheduleAlarm) {
        updateState(uiState.value.copy(preparationAlarm = result.preparationAlarm, departureAlarm = result.departureAlarm))
    }

    private fun onFailedGetScheduleAlarms(e: Throwable) {
        logger.e { "On Failed Get Schedule Alarms: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_SCHEDULE_ALARMS, ToastType.ERROR) }
    }

    /**--------------------------------------------CheckSchedule-----------------------------------------------*/

    @OptIn(ExperimentalTime::class)
    fun createSchedule(isChecked: Boolean, input: String) {
        val (dateRepeat, time, places) = validateScheduleInputs()
        val (date, repeatDays) = dateRepeat
        val (departurePlace, arrivalPlace) = places

        val zone = TimeZone.currentSystemDefault()
        val today = kotlin.time.Clock.System.now().toLocalDateTime(zone).date
        val dateOrToday = date ?: today

        val appointmentAt = DateTimeFormatter.formatIsoDateTime(dateOrToday, time)

        val request = CreateScheduleRequest(
            title = uiState.value.scheduleTitle,
            isRepeat = uiState.value.isRepeat,
            repeatDays = uiState.value.activeWeekDays.map { it + 1 }.toList(),
            isMedicationRequired = isChecked,
            preparationNote = input,
            departurePlace = departurePlace,
            arrivalPlace = arrivalPlace,
            appointmentAt = appointmentAt,
            preparationAlarm = uiState.value.preparationAlarm,
            departureAlarm = uiState.value.departureAlarm
        )

        viewModelScope.launch {
            scheduleRepository.createSchedule(request = request).collect {
                resultResponse(it, ::onSuccessCreateSchedule, ::onFailedCreateSchedule)
            }
        }
    }

    private fun onSuccessCreateSchedule(result: Unit) {
        emitEventFlow(GeneralScheduleEvent.NavigateToMain)
    }

    private fun onFailedCreateSchedule(e: Throwable) {
        logger.e { "On Failed Create Schedule: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_CREATE_SCHEDULE, ToastType.ERROR) }
    }

    fun updateScheduleTitle(title: String) {
        updateState(uiState.value.copy(scheduleTitle = title))
    }

    fun updatePreparationAlarmEnabled() {
        updateState(uiState.value.copy(preparationAlarm = uiState.value.preparationAlarm.copy(enabled = !uiState.value.preparationAlarm.enabled)))
    }

    fun updateBottomSheetVisible(visible: Boolean) {
        updateState(uiState.value.copy(showBottomSheet = visible))
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
                getScheduleAlarms()
                emitEventFlow(GeneralScheduleEvent.NavigateToRouteLoading)
            }
        }
    }

    fun onClickBackButton() {
        when (uiState.value.currentStep) {
            2 -> {
                updateState(uiState.value.copy(currentStep = uiState.value.currentStep - 1))
            }
        }
    }

    private fun validateScheduleInputs(): Triple<Pair<LocalDate?, Set<Int>>, LocalTime, Pair<AddressInfo, AddressInfo>> {
        val time = requireNotNull(uiState.value.selectedTime) { "selectedTime가 null입니다." }
        val from = requireNotNull(uiState.value.selectedDeparturePlace) { "selectedDeparturePlace가 null입니다." }
        val to   = requireNotNull(uiState.value.selectedArrivalPlace)   { "selectedArrivalPlace가 null입니다." }

        val date = uiState.value.selectedDate
        val repeatDays = uiState.value.activeWeekDays

        require(!(date == null && repeatDays.isEmpty())) { "date가 null이면 repeatDays는 비어 있으면 안 됩니다." }

        return Triple(date to repeatDays, time, from to to)
    }
}