package com.dh.ondot.presentation.setting

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.model.response.HomeAddressInfo
import com.dh.ondot.domain.model.ui.UserAnswer
import com.dh.ondot.presentation.ui.theme.DELETION_ANSWER1
import com.dh.ondot.presentation.ui.theme.DELETION_ANSWER2
import com.dh.ondot.presentation.ui.theme.DELETION_ANSWER3
import com.dh.ondot.presentation.ui.theme.DELETION_ANSWER4

data class SettingUiState(

    // 집 주소 설정
    val homeAddress: HomeAddressInfo = HomeAddressInfo(),
    val addressList: List<AddressInfo> = listOf(),
    val selectedHomeAddress: AddressInfo = AddressInfo(),

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
