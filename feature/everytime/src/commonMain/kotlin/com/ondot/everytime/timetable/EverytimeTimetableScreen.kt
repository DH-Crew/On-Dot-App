package com.ondot.everytime.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.presentation.ui.theme.TIMETABLE_SELECT_GUIDE
import com.dh.ondot.presentation.ui.theme.TIMETABLE_SELECT_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.TIMETABLE_SELECT_SCREEN_TITLE
import com.dh.ondot.presentation.ui.theme.TIMETABLE_SELECT_TITLE
import com.dh.ondot.presentation.ui.theme.WORD_NEXT
import com.ondot.designsystem.components.OnDotButton
import com.ondot.designsystem.components.OnDotHighlightText
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.components.topbar.CommonTopBar
import com.ondot.designsystem.components.topbar.model.TopBarStyle
import com.ondot.designsystem.theme.OnDotColor.Gray300
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.everytime.component.timetable.TimetableGrid
import com.ondot.everytime.contract.EverytimeIntent
import com.ondot.everytime.contract.EverytimeSideEffect
import com.ondot.everytime.contract.EverytimeUiState
import com.ondot.everytime.contract.EverytimeViewModel
import com.ondot.everytime.contract.TimetableClassUiModel
import com.ondot.ui.util.ToastManager
import com.ondot.ui.util.buttonPadding
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EverytimeTimetableRoute(
    viewModel: EverytimeViewModel = koinViewModel(),
    popScreen: () -> Unit,
    navigateToPlacePicker: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is EverytimeSideEffect.ShowToast -> {
                    ToastManager.show(
                        message = sideEffect.message,
                        type = sideEffect.toastType,
                    )
                }

                EverytimeSideEffect.NavigateToPlacePicker -> {
                    navigateToPlacePicker()
                }

                else -> Unit
            }
        }
    }

    EverytimeTimetableScreen(
        uiState = uiState,
        onBack = popScreen,
        onSelectClass = { viewModel.dispatch(EverytimeIntent.SelectClass(it)) },
        onNext = { viewModel.dispatch(EverytimeIntent.ValidateSelectedClass) },
    )
}

@Composable
private fun EverytimeTimetableScreen(
    uiState: EverytimeUiState,
    onBack: () -> Unit,
    onSelectClass: (TimetableClassUiModel) -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Gray900)
                .padding(horizontal = 22.dp),
    ) {
        CommonTopBar(
            style = TopBarStyle.BackCenterTitle(title = TIMETABLE_SELECT_SCREEN_TITLE),
            onClick = onBack,
        )

        Spacer(Modifier.height(24.dp))

        OnDotHighlightText(
            text = TIMETABLE_SELECT_TITLE,
            highlight = TIMETABLE_SELECT_HIGHLIGHT,
            style = OnDotTextStyle.TitleMediumM,
        )

        Spacer(Modifier.height(16.dp))

        OnDotText(
            text = TIMETABLE_SELECT_GUIDE,
            style = OnDotTextStyle.BodyMediumR,
            color = Gray300,
        )

        Spacer(Modifier.height(28.dp))

        TimetableGrid(
            classes = uiState.classes,
            modifier = Modifier.weight(1f),
            onClickClass = onSelectClass,
        )

        Spacer(Modifier.height(32.dp))

        OnDotButton(
            buttonType = if (uiState.canGoNext) ButtonType.Green500 else ButtonType.Gray300,
            buttonText = WORD_NEXT,
            onClick = onNext,
            modifier = Modifier.buttonPadding(),
        )
    }
}
