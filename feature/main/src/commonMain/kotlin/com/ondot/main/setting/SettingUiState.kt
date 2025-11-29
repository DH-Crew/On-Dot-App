package com.ondot.main.setting

import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.DELETION_ANSWER1
import com.dh.ondot.presentation.ui.theme.DELETION_ANSWER2
import com.dh.ondot.presentation.ui.theme.DELETION_ANSWER3
import com.dh.ondot.presentation.ui.theme.DELETION_ANSWER4
import com.ondot.design_system.getPlatform
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.HomeAddressInfo
import com.ondot.domain.model.ui.UserAnswer
import com.ondot.ui.base.UiState

data class SettingUiState(

    // 집 주소 설정
    val homeAddress: HomeAddressInfo = HomeAddressInfo(),
    val addressList: List<AddressInfo> = listOf(),
    val selectedHomeAddress: AddressInfo = AddressInfo(),

    // 길 안내 지도 설정
    val mapProviders: List<MapProvider> =
        if (getPlatform() == ANDROID) listOf(
            MapProvider.KAKAO,
            MapProvider.NAVER,
        )
        else  listOf(
            MapProvider.KAKAO,
            MapProvider.NAVER,
            MapProvider.APPLE
        ),
    val selectedProvider: MapProvider = MapProvider.KAKAO,

    // 준비 시간 설정
    val hourInput: String = "",
    val minuteInput: String = "",

    // 로그아웃
    val showLogoutDialog: Boolean = false,

    // 회원탈퇴
    val showDeleteAccountDialog: Boolean = false,
    val accountDeletionReasons: List<UserAnswer> = listOf(
        UserAnswer(1, DELETION_ANSWER1),
        UserAnswer(2, DELETION_ANSWER2),
        UserAnswer(3, DELETION_ANSWER3),
        UserAnswer(4, DELETION_ANSWER4)
    ),
    val selectedReasonIndex: Int = 0,
    val userInput: String = "",
): UiState
