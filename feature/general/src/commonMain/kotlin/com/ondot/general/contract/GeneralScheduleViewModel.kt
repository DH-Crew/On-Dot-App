package com.ondot.general.contract

import androidx.lifecycle.viewModelScope
import com.dh.ondot.presentation.ui.theme.ERROR_CREATE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_GET_HOME_ADDRESS
import com.dh.ondot.presentation.ui.theme.ERROR_GET_PLACE_HISTORY
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_ALARMS
import com.dh.ondot.presentation.ui.theme.ERROR_SEARCH_PLACE
import com.ondot.domain.model.enums.RouterType
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.enums.TransportType
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.HomeAddressInfo
import com.ondot.domain.model.member.PlaceHistory
import com.ondot.domain.model.request.CreateScheduleRequest
import com.ondot.domain.model.request.ScheduleAlarmRequest
import com.ondot.domain.model.schedule.ScheduleAlarm
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.PlaceRepository
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.ui.base.mvi.BaseViewModel
import com.ondot.util.DateTimeFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
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
) : BaseViewModel<GeneralScheduleState, GeneralScheduleIntent, GeneralScheduleSideEffect>(GeneralScheduleState()) {
    private val fullWeek = (0..6).toList()
    private val weekDays = (1..5).toList()
    private val weekend = listOf(0, 6)
    private val query = MutableStateFlow("")
    private var searchPlaceJob: Job? = null

    init {
        viewModelScope.launch {
            query
                .debounce(100)
                .distinctUntilChanged()
                .onEach { value ->
                    if (value.isBlank()) {
                        searchPlaceJob?.cancel()
                        reduce {
                            copy(
                                placePickerState = placePickerState.copy(placeList = emptyList()),
                            )
                        }
                    }
                }.filter { it.isNotBlank() }
                .collect { q ->
                    searchPlace(q)
                }
        }
    }

    override suspend fun handleIntent(intent: GeneralScheduleIntent) {
        when (intent) {
            GeneralScheduleIntent.InitStep -> initStep()
            is GeneralScheduleIntent.ToggleRepeat -> toggleRepeat(intent.isRepeat)
            is GeneralScheduleIntent.SelectRepeatPreset -> selectRepeatPreset(intent.index)
            is GeneralScheduleIntent.ToggleWeekDay -> toggleWeekDay(intent.index)
            GeneralScheduleIntent.ToggleCalendar -> toggleCalendar()
            GeneralScheduleIntent.MoveToPreviousMonth -> moveToPreviousMonth()
            GeneralScheduleIntent.MoveToNextMonth -> moveToNextMonth()
            is GeneralScheduleIntent.SelectDate -> selectDate(intent.date)
            GeneralScheduleIntent.ToggleTimeDial -> toggleTimeDial()
            is GeneralScheduleIntent.SelectTime -> selectTime(intent.time)
            GeneralScheduleIntent.InitHomeAddress -> fetchHomeAddress()
            GeneralScheduleIntent.InitPlaceHistory -> fetchPlaceHistory()
            is GeneralScheduleIntent.SetFocusedRouterType -> setFocusedRouterType(intent.type)
            is GeneralScheduleIntent.UpdateRouteInput -> updateRouteInput(intent.input)
            is GeneralScheduleIntent.SelectPlace -> selectPlace(intent.place)
            is GeneralScheduleIntent.SelectHistory -> selectHistory(intent.history)
            is GeneralScheduleIntent.DeleteHistory -> deleteHistory(intent.history)
            GeneralScheduleIntent.ToggleHomeDeparture -> toggleHomeDeparture()
            is GeneralScheduleIntent.SetInitialPlacePicker -> setInitialPlacePicker(intent.isInitial)
            GeneralScheduleIntent.ClickNext -> clickNext()
            GeneralScheduleIntent.ClickBack -> clickBack()
            is GeneralScheduleIntent.UpdateScheduleTitle -> updateScheduleTitle(intent.title)
            GeneralScheduleIntent.TogglePreparationAlarm -> togglePreparationAlarm()
            is GeneralScheduleIntent.SetBottomSheetVisible -> setBottomSheetVisible(intent.visible)
            is GeneralScheduleIntent.CreateSchedule -> createSchedule(intent.isMedicationRequired, intent.preparationNote)
            is GeneralScheduleIntent.UpdateTransportType -> setTransportType(intent.type)
        }
    }

    private fun initStep() {
        reduce {
            copy(
                totalStep = 2,
                currentStep = 1,
                placePickerState = placePickerState.copy(steps = Pair(1, 2))
            )
        }
    }

    private fun toggleRepeat(newValue: Boolean) {
        reduce {
            copy(
                isRepeat = newValue,
                selectedDate = null,
                activeCheckChip = if (newValue) activeCheckChip else null,
                activeWeekDays = if (newValue) activeWeekDays else emptySet(),
            )
        }
    }

    private fun selectRepeatPreset(index: Int) {
        reduce {
            copy(
                activeCheckChip = index,
                isActiveCalendar = true,
                activeWeekDays =
                    when (index) {
                        0 -> fullWeek.toSet()
                        1 -> weekDays.toSet()
                        2 -> weekend.toSet()
                        else -> emptySet()
                    },
            )
        }
    }

    private fun toggleWeekDay(index: Int) {
        val nextActiveWeekDays =
            currentState.activeWeekDays
                .toMutableSet()
                .apply {
                    if (contains(index)) remove(index) else add(index)
                }.toSet()

        reduce {
            copy(
                isActiveCalendar = true,
                activeWeekDays = nextActiveWeekDays,
                activeCheckChip =
                    when (nextActiveWeekDays) {
                        fullWeek.toSet() -> 0
                        weekDays.toSet() -> 1
                        weekend.toSet() -> 2
                        else -> null
                    },
            )
        }
    }

    private fun toggleCalendar() {
        reduce { copy(isActiveCalendar = !isActiveCalendar) }
    }

    private fun moveToPreviousMonth() {
        reduce { copy(calendarMonth = calendarMonth.minus(DatePeriod(months = 1))) }
    }

    private fun moveToNextMonth() {
        reduce { copy(calendarMonth = calendarMonth.plus(DatePeriod(months = 1))) }
    }

    private fun selectDate(date: LocalDate) {
        reduce { copy(selectedDate = date, isActiveDial = true) }
    }

    private fun toggleTimeDial() {
        reduce { copy(isActiveDial = !isActiveDial) }
    }

    private fun selectTime(time: LocalTime) {
        reduce { copy(selectedTime = time) }
    }

    private fun searchPlace(query: String) {
        searchPlaceJob?.cancel()
        searchPlaceJob =
            launchResult(
                block = { placeRepository.searchPlaceAppResult(query) },
                onSuccess = { places ->
                    reduce {
                        copy(
                            placePickerState = placePickerState.copy(placeList = places),
                        )
                    }
                },
                onError = {
                    emitEffect(GeneralScheduleSideEffect.ShowToast(ERROR_SEARCH_PLACE, ToastType.ERROR))
                },
            )
    }

    private fun fetchHomeAddress() {
        launchResult(
            block = { memberRepository.fetchHomeAddress() },
            onSuccess = ::onSuccessGetHomeAddress,
            onError = {
                emitEffect(GeneralScheduleSideEffect.ShowToast(ERROR_GET_HOME_ADDRESS, ToastType.ERROR))
            },
        )
    }

    private fun onSuccessGetHomeAddress(result: HomeAddressInfo) {
        reduce {
            copy(
                placePickerState =
                    placePickerState.copy(
                        homeAddress =
                            AddressInfo(
                                title = result.roadAddress,
                                roadAddress = result.roadAddress,
                                latitude = result.latitude,
                                longitude = result.longitude,
                            ),
                    ),
                isHomeAddressInitialized = true,
            )
        }
    }

    private fun setFocusedRouterType(type: RouterType) {
        reduce { copy(placePickerState = placePickerState.copy(lastFocusedTextField = type)) }
    }

    private fun updateRouteInput(value: String) {
        when (currentState.placePickerState.lastFocusedTextField) {
            RouterType.Departure ->
                reduce {
                    copy(
                        placePickerState =
                            placePickerState.copy(
                                isChecked = placePickerState.isChecked && value.isHomeAddressInput(),
                                departurePlaceInput = value,
                                selectedDeparturePlace = null,
                            ),
                    )
                }

            RouterType.Arrival ->
                reduce {
                    copy(
                        placePickerState =
                            placePickerState.copy(
                                arrivalPlaceInput = value,
                                selectedArrivalPlace = null,
                            ),
                    )
                }
        }

        query.value = value
    }

    private fun selectPlace(place: AddressInfo) {
        savePlaceHistory(place)

        when (currentState.placePickerState.lastFocusedTextField) {
            RouterType.Departure -> {
                reduce {
                    copy(
                        placePickerState =
                            placePickerState.copy(
                                placeList = emptyList(),
                                isChecked = placePickerState.isChecked && place.isHomeAddress(),
                                departurePlaceInput = place.title,
                                selectedDeparturePlace = place,
                            ),
                    )
                }
                tryEmitEffect(GeneralScheduleSideEffect.RequestArrivalFocus)
                setFocusedRouterType(RouterType.Arrival)
                query.value = ""
            }

            RouterType.Arrival ->
                reduce {
                    copy(
                        placePickerState =
                            placePickerState.copy(
                                placeList = emptyList(),
                                arrivalPlaceInput = place.title,
                                selectedArrivalPlace = place,
                            ),
                    )
                }
        }
    }

    private fun selectHistory(place: PlaceHistory) {
        selectPlace(
            AddressInfo(
                title = place.title,
                roadAddress = place.roadAddress,
                latitude = place.latitude,
                longitude = place.longitude,
            ),
        )
    }

    private fun toggleHomeDeparture() {
        val curValue = currentState.placePickerState.isChecked

        if (!curValue &&
            currentState.placePickerState.homeAddress.title
                .isBlank()
        ) {
            fetchHomeAddress()
            return
        }

        reduce {
            copy(
                placePickerState =
                    placePickerState.copy(
                        isChecked = !curValue,
                        departurePlaceInput = if (!curValue) placePickerState.homeAddress.roadAddress else "",
                        selectedDeparturePlace = if (!curValue) placePickerState.homeAddress else null,
                    ),
            )
        }
    }

    private fun String.isHomeAddressInput(): Boolean {
        val homeAddress = currentState.placePickerState.homeAddress
        return this == homeAddress.roadAddress || this == homeAddress.title
    }

    private fun AddressInfo.isHomeAddress(): Boolean {
        val homeAddress = currentState.placePickerState.homeAddress
        return roadAddress == homeAddress.roadAddress &&
            latitude == homeAddress.latitude &&
            longitude == homeAddress.longitude
    }

    private fun setInitialPlacePicker(value: Boolean) {
        reduce { copy(isInitialPlacePicker = value) }
    }

    private fun fetchPlaceHistory() {
        launchResult(
            block = { placeRepository.fetchHistory() },
            onSuccess = { result ->
                reduce { copy(placePickerState = placePickerState.copy(placeHistory = result)) }
            },
            onError = {
                emitEffect(GeneralScheduleSideEffect.ShowToast(ERROR_GET_PLACE_HISTORY, ToastType.ERROR))
            },
        )
    }

    private fun savePlaceHistory(place: AddressInfo) {
        launchResult(
            block = { placeRepository.saveHistory(place) },
            onSuccess = { fetchPlaceHistory() },
        )
    }

    private fun deleteHistory(place: PlaceHistory) {
        launchResult(
            block = { placeRepository.deleteHistory(place.searchedAt) },
            onSuccess = { fetchPlaceHistory() },
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun fetchScheduleAlarms() {
        val (dateRepeat, time, places) = validateScheduleInputs()
        val (date, _) = dateRepeat
        val (departurePlace, arrivalPlace) = places

        val today =
            kotlin.time.Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        val appointmentAt = DateTimeFormatter.formatIsoDateTime(date ?: today, time)

        launchResult(
            block = {
                scheduleRepository.fetchScheduleAlarms(
                    request =
                        ScheduleAlarmRequest(
                            appointmentAt = appointmentAt,
                            startLatitude = departurePlace.latitude,
                            startLongitude = departurePlace.longitude,
                            endLatitude = arrivalPlace.latitude,
                            endLongitude = arrivalPlace.longitude,
                        ),
                )
            },
            onSuccess = ::onSuccessGetScheduleAlarms,
            onError = {
                emitEffect(GeneralScheduleSideEffect.ShowToast(ERROR_GET_SCHEDULE_ALARMS, ToastType.ERROR))
            },
        )
    }

    private fun onSuccessGetScheduleAlarms(result: ScheduleAlarm) {
        reduce {
            copy(
                preparationAlarm = result.preparationAlarm,
                departureAlarm = result.departureAlarm,
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun createSchedule(
        isMedicationRequired: Boolean,
        preparationNote: String,
    ) {
        val (dateRepeat, time, places) = validateScheduleInputs()
        val (date, _) = dateRepeat
        val (departurePlace, arrivalPlace) = places

        val today =
            kotlin.time.Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        val appointmentAt = DateTimeFormatter.formatIsoDateTime(date ?: today, time)

        val request =
            CreateScheduleRequest(
                title = currentState.scheduleTitle,
                isRepeat = currentState.isRepeat,
                repeatDays = currentState.activeWeekDays.map { it + 1 },
                isMedicationRequired = isMedicationRequired,
                preparationNote = preparationNote,
                departurePlace = departurePlace,
                arrivalPlace = arrivalPlace,
                appointmentAt = appointmentAt,
                preparationAlarm = currentState.preparationAlarm,
                departureAlarm = currentState.departureAlarm,
                transportType = currentState.placePickerState.selectedTransportType.name,
            )

        launchResult(
            block = { scheduleRepository.createScheduleAppResult(request) },
            onSuccess = {
                emitEffect(GeneralScheduleSideEffect.NavigateToMain)
            },
            onError = {
                emitEffect(GeneralScheduleSideEffect.ShowToast(ERROR_CREATE_SCHEDULE, ToastType.ERROR))
            },
        )
    }

    private fun updateScheduleTitle(title: String) {
        reduce { copy(scheduleTitle = title) }
    }

    private fun togglePreparationAlarm() {
        reduce { copy(preparationAlarm = preparationAlarm.copy(enabled = !preparationAlarm.enabled)) }
    }

    private fun setBottomSheetVisible(visible: Boolean) {
        reduce { copy(showBottomSheet = visible) }
    }

    private fun setTransportType(type: TransportType) {
        reduce {
            copy(
                placePickerState = placePickerState.copy(selectedTransportType = type)
            )
        }
    }

    private suspend fun clickNext() {
        when (currentState.currentStep) {
            1 -> {
                emitEffect(GeneralScheduleSideEffect.NavigateToPlacePicker)
                reduce {
                    copy(
                        currentStep = currentStep + 1,
                        placePickerState = placePickerState.copy(steps = Pair(currentStep + 1, totalStep))
                    )
                }
            }

            2 -> {
                fetchScheduleAlarms()
                emitEffect(GeneralScheduleSideEffect.NavigateToRouteLoading)
            }
        }
    }

    private fun clickBack() {
        when (currentState.currentStep) {
            2 -> reduce { copy(currentStep = currentStep - 1) }
        }
    }

    private fun validateScheduleInputs(): Triple<Pair<LocalDate?, Set<Int>>, LocalTime, Pair<AddressInfo, AddressInfo>> {
        val time = requireNotNull(currentState.selectedTime) { "selectedTime가 null입니다." }
        val from = requireNotNull(currentState.placePickerState.selectedDeparturePlace) { "selectedDeparturePlace가 null입니다." }
        val to = requireNotNull(currentState.placePickerState.selectedArrivalPlace) { "selectedArrivalPlace가 null입니다." }

        val date = currentState.selectedDate
        val repeatDays = currentState.activeWeekDays

        require(!(date == null && repeatDays.isEmpty())) { "date가 null이면 repeatDays는 비어 있으면 안 됩니다." }

        return Triple(date to repeatDays, time, from to to)
    }
}
