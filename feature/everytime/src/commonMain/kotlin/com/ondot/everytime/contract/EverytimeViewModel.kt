package com.ondot.everytime.contract

import androidx.lifecycle.viewModelScope
import com.dh.ondot.presentation.ui.theme.ERROR_CREATE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_CREATE_SCHEDULE_EMPTY_PLACE
import com.dh.ondot.presentation.ui.theme.ERROR_EMPTY_URL
import com.dh.ondot.presentation.ui.theme.ERROR_GET_PLACE_HISTORY
import com.dh.ondot.presentation.ui.theme.ERROR_SEARCH_PLACE
import com.dh.ondot.presentation.ui.theme.ERROR_SELECT_FIRST_CLASS
import com.dh.ondot.presentation.ui.theme.ERROR_VALIDATE_EVERYTIME_TIMETABLE
import com.dh.ondot.presentation.ui.theme.SUCCESS_VALIDATE_TIMETABLE
import com.ondot.domain.model.command.CreateEverytimeScheduleCommand
import com.ondot.domain.model.command.SelectedLecture
import com.ondot.domain.model.enums.DayOfWeekKey
import com.ondot.domain.model.enums.RouterType
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.enums.TransportType
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.PlaceHistory
import com.ondot.domain.model.schedule.EverytimeValidateTimetable
import com.ondot.domain.model.schedule.TimetableEntry
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.PlaceRepository
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.ui.base.mvi.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class EverytimeViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val placeRepository: PlaceRepository,
    private val memberRepository: MemberRepository,
) : BaseViewModel<EverytimeUiState, EverytimeIntent, EverytimeSideEffect>(EverytimeUiState()) {
    private val query = MutableStateFlow("")

    init {
        fetchHomeAddress()
        viewModelScope.launch {
            query
                .debounce(100)
                .distinctUntilChanged()
                .onEach { value ->
                    if (value.isBlank()) {
                        reduce { copy(placePickerState = currentState.placePickerState.copy(placeList = emptyList())) }
                    }
                }.filter { it.isNotBlank() }
                .flatMapLatest { q ->
                    placeRepository.searchPlace(q)
                }.collect { response ->
                    response.fold(
                        onSuccess = { reduce { copy(placePickerState = currentState.placePickerState.copy(placeList = it)) } },
                        onFailure = { emitEffect(EverytimeSideEffect.ShowToast(ERROR_SEARCH_PLACE, ToastType.ERROR)) },
                    )
                }
        }
    }

    override suspend fun handleIntent(intent: EverytimeIntent) {
        when (intent) {
            is EverytimeIntent.Validate -> validate(intent.url)
            EverytimeIntent.ValidateSelectedClass -> validateSelectedClass()
            is EverytimeIntent.SelectClass -> selectClass(intent.item)
            EverytimeIntent.CreateSchedule -> createSchedule()
            EverytimeIntent.ToggleCheckBox -> toggleCheckBox()
            is EverytimeIntent.DeleteHistory -> deleteHistory(intent.history)
            is EverytimeIntent.SelectHistory -> selectHistory(intent.history)
            is EverytimeIntent.SetFocusedRouterType -> setLastFocusedTextField(intent.type)
            is EverytimeIntent.UpdateRouteInput -> updateRouteInput(intent.input)
            is EverytimeIntent.SetSelectedPlace -> setSelectedPlace(intent.place)
            EverytimeIntent.InitPlaceHistory -> fetchHistory()
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

    // 에브리타임 시간표 선택 유효성 검사
    private fun validateSelectedClass() {
        if (currentState.canGoNext.not()) {
            tryEmitEffect(
                EverytimeSideEffect.ShowToast(
                    ERROR_SELECT_FIRST_CLASS,
                    ToastType.ERROR,
                ),
            )
            return
        }

        tryEmitEffect(EverytimeSideEffect.NavigateToPlacePicker)
    }

    // 에브리타임 시간표 기반 일정 생성
    @OptIn(ExperimentalTime::class)
    private suspend fun createSchedule() {
        val departure = currentState.placePickerState.selectedDeparturePlace
        val arrival = currentState.placePickerState.selectedArrivalPlace

        if (departure == null || arrival == null) {
            emitEffect(
                EverytimeSideEffect.ShowToast(
                    ERROR_CREATE_SCHEDULE_EMPTY_PLACE,
                    ToastType.ERROR,
                ),
            )
            return
        }

        emitEffect(EverytimeSideEffect.NavigateToRouteLoading)

        val selectedLectures = currentState.toSelectedLectures()

        val command =
            CreateEverytimeScheduleCommand(
                selectedLectures = selectedLectures,
                departurePlace = departure,
                arrivalPlace = arrival,
                transportType = TransportType.PUBLIC_TRANSPORT,
            )

        launchResult(
            block = {
                scheduleRepository.createEverytimeSchedule(command)
            },
            onSuccess = {},
            onError = {
                emitEffect(
                    EverytimeSideEffect.ShowToast(
                        ERROR_CREATE_SCHEDULE,
                        ToastType.ERROR,
                    ),
                )
            },
        )
    }

    // 집에서 출발해요 체크박스 토글
    private fun toggleCheckBox() {
        val curValue = currentState.placePickerState.isChecked

        if (curValue.not() &&
            currentState.placePickerState.homeAddress.title
                .isBlank()
        ) {
            fetchHomeAddress()
            return
        }

        reduce {
            copy(
                placePickerState =
                    currentState.placePickerState.copy(
                        isChecked = !curValue,
                        departurePlaceInput = if (!curValue) currentState.placePickerState.homeAddress.roadAddress else "",
                        selectedDeparturePlace = if (!curValue) currentState.placePickerState.homeAddress else null,
                    ),
            )
        }
    }

    // 집 주소 조회
    private fun fetchHomeAddress() {
        launchResult(
            block = { memberRepository.fetchHomeAddress() },
            onSuccess = { result ->
                reduce {
                    copy(
                        placePickerState =
                            currentState.placePickerState.copy(
                                homeAddress =
                                    AddressInfo(
                                        title = result.roadAddress,
                                        roadAddress = result.roadAddress,
                                        latitude = result.latitude,
                                        longitude = result.longitude,
                                    ),
                            ),
                    )
                }
            },
        )
    }

    // 히스토리 조회
    private fun fetchHistory() {
        launchResult(
            block = { placeRepository.fetchHistory() },
            onSuccess = { result ->
                reduce { copy(placePickerState = currentState.placePickerState.copy(placeHistory = result)) }
            },
            onError = {
                emitEffect(EverytimeSideEffect.ShowToast(ERROR_GET_PLACE_HISTORY, ToastType.ERROR))
            },
        )
    }

    // 히스토리 삭제
    private fun deleteHistory(place: PlaceHistory) {
        launchResult(
            block = { placeRepository.deleteHistory(place.searchedAt) },
            onSuccess = { fetchHistory() },
        )
    }

    // 히스토리 저장
    private fun saveHistory(place: AddressInfo) {
        launchResult(
            block = { placeRepository.saveHistory(place) },
            onSuccess = { fetchHistory() },
        )
    }

    // 히스토리 선택
    private fun selectHistory(history: PlaceHistory) {
        val address =
            AddressInfo(
                title = history.title,
                roadAddress = history.roadAddress,
                latitude = history.latitude,
                longitude = history.longitude,
            )

        setSelectedPlace(address)
    }

    // 장소 선택
    private fun setSelectedPlace(place: AddressInfo) {
        saveHistory(place)

        when (currentState.placePickerState.lastFocusedTextField) {
            RouterType.Departure -> {
                reduce {
                    copy(
                        placePickerState =
                            currentState.placePickerState.copy(
                                placeList = emptyList(),
                                departurePlaceInput = place.title,
                                selectedDeparturePlace = place,
                            ),
                    )
                }
                tryEmitEffect(EverytimeSideEffect.RequestArrivalFocus)
                setLastFocusedTextField(RouterType.Arrival)
                query.value = ""
            }
            RouterType.Arrival -> {
                reduce {
                    copy(
                        placePickerState =
                            currentState.placePickerState.copy(
                                placeList = emptyList(),
                                arrivalPlaceInput = place.title,
                                selectedArrivalPlace = place,
                            ),
                    )
                }
            }
        }
    }

    // 가장 최근 활성화 된 텍스트 필드 타입 저장
    private fun setLastFocusedTextField(type: RouterType) {
        reduce { copy(placePickerState = currentState.placePickerState.copy(lastFocusedTextField = type)) }
    }

    // 텍스트 필드 입력값 저장
    private fun updateRouteInput(value: String) {
        when (currentState.placePickerState.lastFocusedTextField) {
            RouterType.Departure -> {
                reduce {
                    copy(
                        placePickerState =
                            currentState.placePickerState.copy(
                                departurePlaceInput = value,
                                selectedDeparturePlace = null,
                            ),
                    )
                }
            }
            RouterType.Arrival -> {
                reduce {
                    copy(
                        placePickerState =
                            currentState.placePickerState.copy(
                                arrivalPlaceInput = value,
                                selectedArrivalPlace = null,
                            ),
                    )
                }
            }
        }
        query.value = value
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

    // selectedLecture 조립용 유틸 메서드
    private fun EverytimeUiState.toSelectedLectures(): List<SelectedLecture> =
        classes
            .filter { it.isSelected }
            .sortedWith(
                compareBy(
                    { it.day.order },
                    { it.startTime.hour },
                    { it.startTime.minute },
                ),
            ).map {
                SelectedLecture(
                    day = it.day,
                    startTime = it.startTime,
                )
            }
}
