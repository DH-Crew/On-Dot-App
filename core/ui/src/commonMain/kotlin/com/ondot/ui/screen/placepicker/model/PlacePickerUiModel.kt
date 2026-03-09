package com.ondot.ui.screen.placepicker.model

import androidx.compose.runtime.Immutable
import com.ondot.domain.model.enums.RouterType
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.PlaceHistory
import com.ondot.util.DateTimeFormatter

@Immutable
data class PlacePickerUiModel(
    val steps: (Pair<Int, Int>)? = null, // 일반 일정 생성 스텝 진행 상태. first: current, second: total
    val isChecked: Boolean = false, // 집에서 출발 체크박스 상태
    val departurePlaceInput: String = "", // 출발지 입력 상태
    val arrivalPlaceInput: String = "", // 도착지 입력 상태,
    val placeList: List<AddressInfo> = emptyList(), // 검색 결과 리스트,
    val placeHistory: List<PlaceHistory> = emptyList(), // 검색 기록 리스트,
    val lastFocusedTextField: RouterType = RouterType.Departure, // 두개의 텍스트 필드 중 가장 마지막으로 포커스된 텍스트 필드
) {
    companion object {
        fun formattedDate(date: String) = DateTimeFormatter.formatKoreanDateMonthDay(date)
    }
}
