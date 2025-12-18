package com.ondot.main.setting.account_deletion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.DELETE_ACCOUNT_CAUTION_TITLE
import com.dh.ondot.presentation.ui.theme.DELETE_ACCOUNT_DIALOG_CONTENT
import com.dh.ondot.presentation.ui.theme.DELETE_ACCOUNT_DIALOG_TITLE
import com.dh.ondot.presentation.ui.theme.DELETE_ACCOUNT_GUIDE1
import com.dh.ondot.presentation.ui.theme.DELETE_ACCOUNT_GUIDE2
import com.dh.ondot.presentation.ui.theme.DELETE_ACCOUNT_GUIDE3
import com.dh.ondot.presentation.ui.theme.DELETE_ACCOUNT_GUIDE_BOLD1
import com.dh.ondot.presentation.ui.theme.DELETE_ACCOUNT_GUIDE_BOLD2
import com.dh.ondot.presentation.ui.theme.DELETE_ACCOUNT_GUIDE_BOLD3
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Red
import com.dh.ondot.presentation.ui.theme.WORD_CANCEL
import com.dh.ondot.presentation.ui.theme.WORD_CONFIRM
import com.dh.ondot.presentation.ui.theme.WORD_DELETE_ACCOUNT_ACTION
import com.dh.ondot.presentation.ui.theme.WORD_WITHDRAW
import com.ondot.design_system.components.OnDotButton
import com.ondot.design_system.components.OnDotDialog
import com.ondot.design_system.components.OnDotText
import com.ondot.design_system.components.TopBar
import com.ondot.design_system.getPlatform
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.TopBarType
import com.ondot.domain.model.ui.UserAnswer
import com.ondot.main.setting.SettingEvent
import com.ondot.main.setting.SettingUiState
import com.ondot.main.setting.SettingViewModel
import com.ondot.util.AnalyticsLogger
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_warning_red
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeleteAccountScreen(
    viewModel: SettingViewModel = koinViewModel(),
    popScreen: () -> Unit,
    navigateToLoginScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect {
            when(it) {
                is SettingEvent.NavigateToLoginScreen -> navigateToLoginScreen()
            }
        }
    }

    DeleteAccountContent(
        uiState = uiState,
        buttonEnabled = viewModel.isButtonEnabled(),
        onBackClick = popScreen,
        onReasonClick = viewModel::onReasonClick,
        onToggleDeleteAccountDialog = viewModel::toggleDeleteAccountDialog,
        onDeleteAccount = viewModel::withdrawUser
    )
}

@Composable
fun DeleteAccountContent(
    uiState: SettingUiState,
    buttonEnabled: Boolean,
    onBackClick: () -> Unit = {},
    onReasonClick: (Int) -> Unit = {},
    onToggleDeleteAccountDialog: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray900)
                .padding(horizontal = 22.dp)
                .padding(bottom = if (getPlatform() == ANDROID) 16.dp else 37.dp)
        ) {
            TopBarSection(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(32.dp))

            DeleteAccountCautionSection()

            Spacer(modifier = Modifier.height(28.dp))

            DeleteAccountGuideSection()

            Spacer(modifier = Modifier.height(24.dp))

            DeleteAccountReasonList(
                reasons = uiState.accountDeletionReasons,
                selectedIndex = uiState.selectedReasonIndex,
                onReasonClick = onReasonClick
            )

            Spacer(modifier = Modifier.weight(1f))

            OnDotButton(
                buttonText = WORD_DELETE_ACCOUNT_ACTION,
                buttonType = if (buttonEnabled) ButtonType.Green500 else ButtonType.Gray300,
                onClick = { if (buttonEnabled) onToggleDeleteAccountDialog() }
            )
        }

        if (uiState.showDeleteAccountDialog) {
            OnDotDialog(
                dialogTitle = DELETE_ACCOUNT_DIALOG_TITLE,
                dialogContent = DELETE_ACCOUNT_DIALOG_CONTENT,
                positiveText = WORD_CONFIRM,
                negativeText = WORD_CANCEL,
                onPositiveClick = onDeleteAccount,
                onNegativeClick = onToggleDeleteAccountDialog,
                onDismiss = onToggleDeleteAccountDialog
            )
        }
    }
}

@Composable
private fun TopBarSection(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TopBar(
            type = TopBarType.BACK,
            onClick = onBackClick
        )

        OnDotText(
            text = WORD_WITHDRAW,
            style = OnDotTextStyle.TitleSmallM,
            color = Gray0,
            modifier = Modifier.padding(top = if (getPlatform() == ANDROID) 50.dp else 70.dp)
        )
    }
}

@Composable
private fun DeleteAccountCautionSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_warning_red),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.width(24.dp))

        OnDotText(
            text = DELETE_ACCOUNT_CAUTION_TITLE,
            style = OnDotTextStyle.BodyLargeSB,
            color = Red,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DeleteAccountGuideSection() {
    val guides = listOf(
        DELETE_ACCOUNT_GUIDE1,
        DELETE_ACCOUNT_GUIDE2,
        DELETE_ACCOUNT_GUIDE3
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        guides.forEachIndexed { index, guide ->
            GuideItem(text = guide)
            if (index != 2) Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GuideItem(text: String) {
    val lines = text.split("\n")

    OnDotText(
        text = buildAnnotatedString {
            append("• ")

            lines.forEachIndexed { index, line ->
                val boldTargets = listOf(
                    DELETE_ACCOUNT_GUIDE_BOLD1,
                    DELETE_ACCOUNT_GUIDE_BOLD2,
                    DELETE_ACCOUNT_GUIDE_BOLD3
                )

                if (boldTargets.any { line.contains(it) }) {
                    boldTargets.forEach { target ->
                        if (line.contains(target)) {
                            val parts = line.split(target)
                            append(parts[0])
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(target)
                            }
                            if (parts.size > 1) append(parts[1])
                        }
                    }
                } else {
                    append(line)
                }

                if (index < lines.lastIndex) append("\n   ")
            }
        },
        style = OnDotTextStyle.BodyMediumR,
        color = Gray0
    )
}

@Composable
private fun DeleteAccountReasonList(
    reasons: List<UserAnswer> = emptyList(),
    selectedIndex: Int,
    onReasonClick: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        reasons.forEachIndexed { index, reason ->
            DeleteAccountReasonItem(
                reason = reason,
                isSelected = index == selectedIndex,
                onClick = { onReasonClick(index) }
            )

            if (index != 3) Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DeleteAccountReasonItem(
    reason: UserAnswer,
    isSelected: Boolean,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = if (isSelected) Green600 else Gray600, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        OnDotText(
            text = reason.content,
            style = OnDotTextStyle.BodyMediumR,
            color = Gray0
        )
    }
}