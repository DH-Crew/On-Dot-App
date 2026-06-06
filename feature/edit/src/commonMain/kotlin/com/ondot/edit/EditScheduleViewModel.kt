package com.ondot.edit

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.presentation.ui.theme.ERROR_DELETE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_EDIT_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_GET_HOME_ADDRESS
import com.dh.ondot.presentation.ui.theme.ERROR_GET_PLACE_HISTORY
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_ALARMS
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_DETAIL
import com.dh.ondot.presentation.ui.theme.ERROR_SEARCH_PLACE
import com.dh.ondot.presentation.ui.theme.ERROR_UPDATE_ALARM
import com.ondot.domain.model.enums.RouterType
import com.ondot.domain.model.enums.TimeBottomSheet
import com.ondot.domain.model.enums.TimeType
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.enums.TransportType
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.HomeAddressInfo
import com.ondot.domain.model.member.PlaceHistory
import com.ondot.domain.model.request.DeletePlaceHistoryRequest
import com.ondot.domain.model.request.ScheduleAlarmRequest
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.schedule.ScheduleAlarm
import com.ondot.domain.model.schedule.ScheduleDetail
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.PlaceRepository
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.domain.service.ScheduleAlarmManager
import com.ondot.ui.base.BaseViewModel
import com.ondot.ui.screen.placepicker.model.PlacePickerUiModel
import com.ondot.ui.util.ToastManager
import com.ondot.util.DateTimeFormatter
import com.ondot.util.DateTimeFormatter.toLocalDateFromIso
import com.ondot.util.DateTimeFormatter.toLocalTimeFromIso
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
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class EditScheduleViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val placeRepository: PlaceRepository,
    private val memberRepository: MemberRepository,
    private val scheduleAlarmManager: ScheduleAlarmManager,
) : BaseViewModel<EditScheduleUiState>(EditScheduleUiState()) {
    private val logger = Logger.withTag("EditScheduleViewModel")
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
                        updatePlacePickerState { copy(placeList = emptyList()) }
                    }
                }.filter { it.isNotBlank() }
                .collect(::searchPlace)
        }
    }

    /**------------------------------------------일정 상세 조회-------------------------------------------------*/

    fun getScheduleDetail(scheduleId: Long) {
        viewModelScope.launch {
            scheduleRepository.getScheduleDetail(scheduleId).collect {
                resultResponse(it, ::onSuccessGetScheduleDetail, ::onFailGetScheduleDetail)
            }
        }
    }

    private fun onSuccessGetScheduleDetail(result: ScheduleDetail) {
        val date = if (result.repeatDays.isEmpty()) result.appointmentAt.toLocalDateFromIso() else null
        val time = result.appointmentAt.toLocalTimeFromIso()

        updateState(
            uiState.value.copy(
                schedule = result.copy(repeatDays = result.repeatDays.map { it - 1 }),
                originalSchedule = result,
                isInitialized = true,
                selectedDate = date,
                selectedTime = time,
                placePickerState =
                    PlacePickerUiModel(
                        departurePlaceInput = result.departurePlace.title,
                        arrivalPlaceInput = result.arrivalPlace.title,
                        selectedDeparturePlace = result.departurePlace,
                        selectedArrivalPlace = result.arrivalPlace,
                        selectedTransportType = result.transportType,
                    ),
            ),
        )
    }

    private fun onFailGetScheduleDetail(e: Throwable) {
        logger.e { "일정 상세 조회 실패: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_SCHEDULE_DETAIL, ToastType.ERROR) }
    }

    /**------------------------------------------일정 수정-------------------------------------------------*/

    @OptIn(ExperimentalTime::class)
    fun saveSchedule() {
        val originalScheduleDetail = uiState.value.originalSchedule ?: return

        val zone = TimeZone.currentSystemDefault()
        val today =
            Clock.System
                .now()
                .toLocalDateTime(zone)
                .date
        val selectedDate = uiState.value.selectedDate ?: today

        val newAppointmentAt =
            DateTimeFormatter.formatIsoDateTime(
                date = selectedDate,
                time =
                    uiState.value.schedule.appointmentAt
                        .toLocalTimeFromIso(),
            )

        val updatedScheduleDetail =
            uiState.value.schedule.copy(
                appointmentAt = newAppointmentAt,
                repeatDays =
                    uiState.value.schedule.repeatDays
                        .map { it + 1 },
            )

        val scheduleId = uiState.value.scheduleId
        val originalSchedule = originalScheduleDetail.toSchedule(scheduleId)
        val updatedSchedule = updatedScheduleDetail.toSchedule(scheduleId)

        viewModelScope.launch {
            val replaceResult = scheduleAlarmManager.replace(originalSchedule, updatedSchedule)
            if (replaceResult.isFailure) {
                logger.e(replaceResult.exceptionOrNull()) { "알람 재스케줄링 실패" }
                ToastManager.show(ERROR_UPDATE_ALARM, ToastType.ERROR)
                return@launch
            }

            var remoteError: Throwable? = null
            scheduleRepository.editSchedule(scheduleId, updatedScheduleDetail).collect { result ->
                result.onFailure { remoteError = it }
            }

            if (remoteError == null) {
                scheduleRepository.upsertLocalSchedule(updatedSchedule)
                emitEventFlow(EditScheduleEvent.NavigateBack)
            } else {
                logger.e(remoteError) { "일정 수정 실패, 알람 롤백 시도" }

                val rollbackResult = scheduleAlarmManager.replace(updatedSchedule, originalSchedule)
                rollbackResult.onFailure { rollbackError ->
                    logger.e(rollbackError) { "알람 롤백 실패" }
                }

                ToastManager.show(ERROR_EDIT_SCHEDULE, ToastType.ERROR)
            }
        }
    }

    /**------------------------------------------일정 삭제-------------------------------------------------*/

    fun deleteSchedule() {
        val targetSchedule = uiState.value.schedule.toSchedule(uiState.value.scheduleId)

        viewModelScope.launch {
            try {
                if (targetSchedule.hasActiveAlarm) {
                    scheduleAlarmManager.cancel(targetSchedule)
                }

                var remoteError: Throwable? = null
                scheduleRepository.deleteSchedule(uiState.value.scheduleId).collect { result ->
                    result.onFailure { remoteError = it }
                }

                if (remoteError == null) {
                    emitEventFlow(EditScheduleEvent.NavigateBack)
                } else {
                    logger.e(remoteError) { "일정 삭제 실패, 알람 복구 시도" }

                    runCatching {
                        if (targetSchedule.hasActiveAlarm) {
                            scheduleAlarmManager.schedule(targetSchedule)
                        }
                    }.onFailure { rollbackError ->
                        logger.e(rollbackError) { "삭제 실패 후 알람 복구 실패" }
                    }

                    ToastManager.show(ERROR_DELETE_SCHEDULE, ToastType.ERROR)
                }
            } catch (t: Throwable) {
                logger.e(t) { "일정 삭제 전 알람 취소 실패" }
                ToastManager.show(ERROR_DELETE_SCHEDULE, ToastType.ERROR)
            }
        }
    }

    /**------------------------------------------상태 변수 처리-------------------------------------------------*/

    fun updateScheduleId(scheduleId: Long) {
        updateState(uiState.value.copy(scheduleId = scheduleId))
    }

    fun updateScheduleTitle(title: String) {
        updateState(uiState.value.copy(schedule = uiState.value.schedule.copy(title = title)))
    }

    fun toggleSwitch() {
        val current = uiState.value
        val prep = current.schedule.preparationAlarm
        updateState(
            current.copy(
                schedule =
                    current.schedule.copy(
                        preparationAlarm = prep.copy(enabled = !prep.enabled),
                    ),
            ),
        )
    }

    fun showDeleteDialog() {
        updateState(uiState.value.copy(showDeleteDialog = true))
    }

    fun hideDeleteDialog() {
        updateState(uiState.value.copy(showDeleteDialog = false))
    }

    fun fetchHomeAddress() {
        viewModelScope.launch {
            memberRepository.getHomeAddress().collect {
                resultResponse(it, ::onSuccessGetHomeAddress, ::onFailGetHomeAddress)
            }
        }
    }

    private fun onSuccessGetHomeAddress(result: HomeAddressInfo) {
        updateState(
            uiState.value.copy(
                placePickerState =
                    uiState.value.placePickerState.copy(
                        homeAddress =
                            AddressInfo(
                                title = result.roadAddress,
                                roadAddress = result.roadAddress,
                                latitude = result.latitude,
                                longitude = result.longitude,
                            ),
                    ),
                isHomeAddressInitialized = true,
            ),
        )
    }

    private fun onFailGetHomeAddress(e: Throwable) {
        logger.e { "집 주소 조회 실패: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_HOME_ADDRESS, ToastType.ERROR) }
    }

    fun fetchPlaceHistory() {
        viewModelScope.launch {
            placeRepository.getPlaceHistory().collect {
                resultResponse(it, ::onSuccessGetPlaceHistory, ::onFailGetPlaceHistory)
            }
        }
    }

    private fun onSuccessGetPlaceHistory(result: List<PlaceHistory>) {
        updatePlacePickerState { copy(placeHistory = result) }
    }

    private fun onFailGetPlaceHistory(e: Throwable) {
        logger.e { "장소 검색 기록 조회 실패: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_PLACE_HISTORY, ToastType.ERROR) }
    }

    fun setFocusedRouterType(type: RouterType) {
        updatePlacePickerState { copy(lastFocusedTextField = type) }
    }

    fun updateRouteInput(value: String) {
        updateRouteInput(uiState.value.placePickerState.lastFocusedTextField, value)
    }

    fun updateRouteInput(
        type: RouterType,
        value: String,
    ) {
        when (type) {
            RouterType.Departure ->
                updatePlacePickerState {
                    copy(
                        lastFocusedTextField = type,
                        isChecked = isChecked && value.isHomeAddressInput(),
                        departurePlaceInput = value,
                        selectedDeparturePlace = null,
                    )
                }

            RouterType.Arrival ->
                updatePlacePickerState {
                    copy(
                        lastFocusedTextField = type,
                        arrivalPlaceInput = value,
                        selectedArrivalPlace = null,
                    )
                }
        }

        query.value = value
    }

    fun selectPlace(place: AddressInfo) {
        savePlaceHistory(place)

        when (uiState.value.placePickerState.lastFocusedTextField) {
            RouterType.Departure -> {
                updatePlacePickerState {
                    copy(
                        placeList = emptyList(),
                        isChecked = isChecked && place.isHomeAddress(),
                        departurePlaceInput = place.title,
                        selectedDeparturePlace = place,
                    )
                }
                emitEventFlow(EditScheduleEvent.RequestArrivalFocus)
                setFocusedRouterType(RouterType.Arrival)
                query.value = ""
            }

            RouterType.Arrival ->
                updatePlacePickerState {
                    copy(
                        placeList = emptyList(),
                        arrivalPlaceInput = place.title,
                        selectedArrivalPlace = place,
                    )
                }
        }
    }

    fun selectHistory(place: PlaceHistory) {
        selectPlace(
            AddressInfo(
                title = place.title,
                roadAddress = place.roadAddress,
                latitude = place.latitude,
                longitude = place.longitude,
            ),
        )
    }

    fun deletePlaceHistory(place: PlaceHistory) {
        viewModelScope.launch {
            placeRepository.deletePlaceHistory(DeletePlaceHistoryRequest(place.searchedAt)).collect {
                resultResponse(it, { fetchPlaceHistory() }, ::onFailGetPlaceHistory)
            }
        }
    }

    fun toggleHomeDeparture() {
        val current = uiState.value.placePickerState

        if (!current.isChecked && current.homeAddress.title.isBlank()) {
            fetchHomeAddress()
            return
        }

        updatePlacePickerState {
            copy(
                isChecked = !current.isChecked,
                departurePlaceInput = if (!current.isChecked) homeAddress.roadAddress else "",
                selectedDeparturePlace = if (!current.isChecked) homeAddress else null,
            )
        }
    }

    fun updateTransportType(type: TransportType) {
        updatePlacePickerState { copy(selectedTransportType = type) }
    }

    fun setInitialPlacePicker(value: Boolean) {
        updateState(uiState.value.copy(isInitialPlacePicker = value))
    }

    fun preparePlacePicker() {
        val current = uiState.value
        val currentSchedule = current.schedule
        val currentPlacePickerState = current.placePickerState
        val isHomeDeparture =
            currentPlacePickerState.homeAddress.title.isNotBlank() &&
                currentSchedule.departurePlace.isHomeAddress()

        updateStateSync(
            current.copy(
                placePickerState =
                    currentPlacePickerState.copy(
                        isChecked = isHomeDeparture,
                        departurePlaceInput = currentSchedule.departurePlace.title,
                        arrivalPlaceInput = currentSchedule.arrivalPlace.title,
                        selectedDeparturePlace = currentSchedule.departurePlace,
                        selectedArrivalPlace = currentSchedule.arrivalPlace,
                        placeList = emptyList(),
                        lastFocusedTextField = RouterType.Departure,
                        selectedTransportType = currentSchedule.transportType,
                    ),
                isInitialPlacePicker = true,
            ),
        )
        query.value = ""
    }

    fun applyRouteChangesAndFetchAlarms(): Boolean {
        val current = uiState.value
        val departurePlace = current.placePickerState.selectedDeparturePlace ?: return false
        val arrivalPlace = current.placePickerState.selectedArrivalPlace ?: return false
        val transportType = current.placePickerState.selectedTransportType
        val hasRouteChanged =
            current.schedule.departurePlace != departurePlace ||
                current.schedule.arrivalPlace != arrivalPlace ||
                current.schedule.transportType != transportType

        updateStateSync(
            current.copy(
                schedule =
                    current.schedule.copy(
                        departurePlace = departurePlace,
                        arrivalPlace = arrivalPlace,
                        transportType = transportType,
                    ),
            ),
        )

        if (hasRouteChanged) {
            fetchScheduleAlarms()
        }

        return hasRouteChanged
    }

    fun editDate(
        isRepeat: Boolean,
        repeatDays: Set<Int>,
        date: LocalDate?,
    ) {
        logger.d { "isRepeat: $isRepeat, repeatDays: $repeatDays, date: $date" }

        if (isRepeat && repeatDays.isEmpty()) return

        val current = uiState.value
        val nextSchedule =
            if (!isRepeat && date != null) {
                current.schedule.shiftDate(date)
            } else {
                current.schedule
            }

        updateStateSync(
            current.copy(
                schedule =
                    nextSchedule.copy(
                        isRepeat = isRepeat,
                        repeatDays = repeatDays.toList(),
                    ),
                selectedDate = if (isRepeat) null else date,
            ),
        )
    }

    fun showDateBottomSheet() {
        updateState(uiState.value.copy(showDateBottomSheet = true))
    }

    fun hideDateBottomSheet() {
        updateState(uiState.value.copy(showDateBottomSheet = false))
    }

    fun editTime(
        newDate: LocalDate,
        newTime: LocalTime,
    ) {
        when (uiState.value.selectedTimeType) {
            TimeType.APPOINTMENT -> {
                updateStateSync(
                    uiState.value.copy(
                        schedule =
                            uiState.value.schedule.copy(
                                appointmentAt =
                                    DateTimeFormatter.formatIsoDateTime(
                                        date = resolveAppointmentDate(),
                                        time = newTime,
                                    ),
                            ),
                    ),
                )
                fetchScheduleAlarms()
            }
            TimeType.DEPARTURE ->
                updateState(
                    uiState.value.copy(
                        schedule =
                            uiState.value.schedule.copy(
                                departureAlarm =
                                    uiState.value.schedule.departureAlarm.copy(
                                        triggeredAt =
                                            DateTimeFormatter.formatIsoDateTime(
                                                date = newDate,
                                                time = newTime,
                                            ),
                                    ),
                            ),
                    ),
                )
            TimeType.PREPARATION ->
                updateState(
                    uiState.value.copy(
                        schedule =
                            uiState.value.schedule.copy(
                                preparationAlarm =
                                    uiState.value.schedule.preparationAlarm.copy(
                                        triggeredAt =
                                            DateTimeFormatter.formatIsoDateTime(
                                                date = newDate,
                                                time = newTime,
                                            ),
                                    ),
                            ),
                    ),
                )
        }
    }

    fun showTimeBottomSheet(timeType: TimeType) {
        val selectedTime =
            when (timeType) {
                TimeType.APPOINTMENT ->
                    uiState.value.schedule.appointmentAt
                        .toLocalTimeFromIso()
                TimeType.DEPARTURE ->
                    uiState.value.schedule.departureAlarm.triggeredAt
                        .toLocalTimeFromIso()
                TimeType.PREPARATION ->
                    uiState.value.schedule.preparationAlarm.triggeredAt
                        .toLocalTimeFromIso()
            }
        val selectedAlarmDate =
            when (timeType) {
                TimeType.DEPARTURE ->
                    runCatching {
                        uiState.value.schedule.departureAlarm.triggeredAt
                            .toLocalDateFromIso()
                    }.getOrNull()
                TimeType.PREPARATION ->
                    runCatching {
                        uiState.value.schedule.preparationAlarm.triggeredAt
                            .toLocalDateFromIso()
                    }.getOrNull()
                else -> null
            }
        val bottomSheetType =
            when (timeType) {
                TimeType.APPOINTMENT -> TimeBottomSheet.Schedule
                TimeType.DEPARTURE -> TimeBottomSheet.Alarm
                TimeType.PREPARATION -> TimeBottomSheet.Alarm
            }

        updateStateSync(
            uiState.value.copy(
                activeTimeBottomSheet = bottomSheetType,
                selectedTimeType = timeType,
                selectedTime = selectedTime,
                selectedAlarmDate = selectedAlarmDate,
            ),
        )
    }

    fun hideTimeBottomSheet() {
        updateState(uiState.value.copy(activeTimeBottomSheet = null))
    }

    private fun searchPlace(query: String) {
        searchPlaceJob?.cancel()
        searchPlaceJob =
            viewModelScope.launch {
                placeRepository.searchPlace(query).collect {
                    resultResponse(it, ::onSuccessSearchPlace, ::onFailSearchPlace)
                }
            }
    }

    private fun onSuccessSearchPlace(result: List<AddressInfo>) {
        updatePlacePickerState { copy(placeList = result) }
    }

    private fun onFailSearchPlace(e: Throwable) {
        logger.e { "장소 검색 실패: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_SEARCH_PLACE, ToastType.ERROR) }
    }

    private fun savePlaceHistory(place: AddressInfo) {
        viewModelScope.launch {
            placeRepository.savePlaceHistory(place).collect {
                resultResponse(it, { fetchPlaceHistory() })
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun fetchScheduleAlarms() {
        val current = uiState.value
        val departurePlace = current.schedule.departurePlace
        val arrivalPlace = current.schedule.arrivalPlace

        if (departurePlace.title.isBlank() || arrivalPlace.title.isBlank()) return

        updateStateSync(current.copy(isAlarmRecalculating = true))

        val zone = TimeZone.currentSystemDefault()
        val today =
            Clock.System
                .now()
                .toLocalDateTime(zone)
                .date
        val selectedDate = current.selectedDate ?: today
        val appointmentAt =
            DateTimeFormatter.formatIsoDateTime(
                date = selectedDate,
                time = current.schedule.appointmentAt.toLocalTimeFromIso(),
            )

        viewModelScope.launch {
            scheduleRepository
                .getScheduleAlarms(
                    ScheduleAlarmRequest(
                        appointmentAt = appointmentAt,
                        startLatitude = departurePlace.latitude,
                        startLongitude = departurePlace.longitude,
                        endLatitude = arrivalPlace.latitude,
                        endLongitude = arrivalPlace.longitude,
                        transportType = current.schedule.transportType.name,
                    ),
                ).collect {
                    resultResponse(it, ::onSuccessGetScheduleAlarms, ::onFailGetScheduleAlarms)
                }
        }
    }

    private fun onSuccessGetScheduleAlarms(result: ScheduleAlarm) {
        updateStateSync(
            uiState.value.copy(
                schedule =
                    uiState.value.schedule.copy(
                        preparationAlarm = result.preparationAlarm,
                        departureAlarm = result.departureAlarm,
                    ),
                isAlarmRecalculating = false,
            ),
        )
    }

    private fun onFailGetScheduleAlarms(e: Throwable) {
        updateStateSync(uiState.value.copy(isAlarmRecalculating = false))
        logger.e { "알람 정보 계산 실패: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_SCHEDULE_ALARMS, ToastType.ERROR) }
    }

    @OptIn(ExperimentalTime::class)
    private fun resolveAppointmentDate(): LocalDate {
        uiState.value.selectedDate?.let { return it }

        return runCatching {
            uiState.value.schedule.appointmentAt
                .toLocalDateFromIso()
        }.getOrElse {
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        }
    }

    private fun updatePlacePickerState(block: PlacePickerUiModel.() -> PlacePickerUiModel) {
        updateStateSync(uiState.value.copy(placePickerState = uiState.value.placePickerState.block()))
    }

    private fun ScheduleDetail.shiftDate(newAppointmentDate: LocalDate): ScheduleDetail {
        val currentAppointmentDate =
            runCatching {
                appointmentAt.toLocalDateFromIso()
            }.getOrNull() ?: return this
        val deltaDays = currentAppointmentDate.daysUntil(newAppointmentDate)

        if (deltaDays == 0) return this

        return copy(
            appointmentAt = appointmentAt.shiftDateBy(deltaDays),
            preparationAlarm = preparationAlarm.copy(triggeredAt = preparationAlarm.triggeredAt.shiftDateBy(deltaDays)),
            departureAlarm = departureAlarm.copy(triggeredAt = departureAlarm.triggeredAt.shiftDateBy(deltaDays)),
        )
    }

    private fun String.shiftDateBy(deltaDays: Int): String =
        runCatching {
            DateTimeFormatter.formatIsoDateTime(
                date = toLocalDateFromIso().plus(DatePeriod(days = deltaDays)),
                time = toLocalTimeFromIso(),
            )
        }.getOrDefault(this)

    private fun String.isHomeAddressInput(): Boolean {
        val homeAddress = uiState.value.placePickerState.homeAddress
        return this == homeAddress.roadAddress || this == homeAddress.title
    }

    private fun AddressInfo.isHomeAddress(): Boolean {
        val homeAddress = uiState.value.placePickerState.homeAddress
        return roadAddress == homeAddress.roadAddress &&
            latitude == homeAddress.latitude &&
            longitude == homeAddress.longitude
    }

    private fun ScheduleDetail.toSchedule(scheduleId: Long): Schedule =
        Schedule(
            scheduleId = scheduleId,
            scheduleTitle = title,
            isRepeat = isRepeat,
            repeatDays = repeatDays,
            appointmentAt = appointmentAt,
            departureAlarm = departureAlarm,
            preparationAlarm = preparationAlarm,
            startLatitude = departurePlace.latitude,
            startLongitude = departurePlace.longitude,
            endLatitude = arrivalPlace.latitude,
            endLongitude = arrivalPlace.longitude,
            hasActiveAlarm = departureAlarm.enabled || preparationAlarm.enabled,
            transportType = transportType,
        )
}
