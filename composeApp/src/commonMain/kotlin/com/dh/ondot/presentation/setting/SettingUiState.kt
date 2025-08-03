package com.dh.ondot.presentation.setting

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.ui.UserAnswer
import com.dh.ondot.presentation.ui.theme.WITHDRAWAL_ANSWER1
import com.dh.ondot.presentation.ui.theme.WITHDRAWAL_ANSWER2
import com.dh.ondot.presentation.ui.theme.WITHDRAWAL_ANSWER3
import com.dh.ondot.presentation.ui.theme.WITHDRAWAL_ANSWER4

data class SettingUiState(

    // 로그아웃
    val showLogoutDialog: Boolean = false,

    // 회원탈퇴
    val withdrawalReason: List<UserAnswer> = listOf(
        UserAnswer(1, WITHDRAWAL_ANSWER1),
        UserAnswer(2, WITHDRAWAL_ANSWER2),
        UserAnswer(3, WITHDRAWAL_ANSWER3),
        UserAnswer(4, WITHDRAWAL_ANSWER4)
    ),
    val selectedWithdrawalReasonIndex: Int = 1,
    val userInput: String = "",
): UiState
