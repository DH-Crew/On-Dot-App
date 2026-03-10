package com.ondot.everytime.urlInput

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.LANDING_SCREEN_TITLE
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray300
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500
import com.dh.ondot.presentation.ui.theme.PASTE_URL
import com.dh.ondot.presentation.ui.theme.URL_INPUT_GUIDE
import com.dh.ondot.presentation.ui.theme.URL_INPUT_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.URL_INPUT_TITLE
import com.dh.ondot.presentation.ui.theme.WORD_NEXT
import com.ondot.designsystem.components.OnDotButton
import com.ondot.designsystem.components.OnDotHighlightText
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.components.RoundedTextField
import com.ondot.designsystem.components.topbar.CommonTopBar
import com.ondot.designsystem.components.topbar.model.TopBarStyle
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.service.ExternalAppLauncher
import com.ondot.everytime.contract.EverytimeIntent
import com.ondot.everytime.contract.EverytimeSideEffect
import com.ondot.everytime.contract.EverytimeViewModel
import com.ondot.ui.util.ToastManager
import com.ondot.ui.util.buttonPadding
import com.ondot.ui.util.noRippleClickable
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EverytimeUrlInputRoute(
    viewModel: EverytimeViewModel = koinViewModel(),
    appLauncher: ExternalAppLauncher = koinInject(),
    popScreen: () -> Unit,
    navigateToTimetable: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is EverytimeSideEffect.ShowToast -> {
                    ToastManager.show(message = sideEffect.message, type = sideEffect.toastType)
                }

                EverytimeSideEffect.NavigateToTimetable -> navigateToTimetable()

                else -> Unit
            }
        }
    }

    EverytimeUrlInputScreen(
        onBack = popScreen,
        onOpenEverytime = { appLauncher.openEverytime() },
        onNext = { viewModel.dispatch(EverytimeIntent.Validate(it)) },
    )
}

@Composable
private fun EverytimeUrlInputScreen(
    onBack: () -> Unit,
    onOpenEverytime: () -> Unit,
    onNext: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Gray900)
                .padding(horizontal = 22.dp),
    ) {
        CommonTopBar(
            style = TopBarStyle.BackCenterTitle(title = LANDING_SCREEN_TITLE),
            onClick = onBack,
        )

        Spacer(Modifier.height(24.dp))

        OnDotHighlightText(
            text = URL_INPUT_TITLE,
            highlight = URL_INPUT_HIGHLIGHT,
            style = OnDotTextStyle.TitleMediumM,
        )

        Spacer(Modifier.height(16.dp))

        OnDotText(
            text = URL_INPUT_GUIDE,
            style = OnDotTextStyle.BodyMediumR,
            color = Gray300,
        )

        Spacer(Modifier.height(40.dp))

        UrlInputTextField(
            input = input,
            onValueChange = { input = it },
        )

        EverytimeOpenTextButton(onClick = onOpenEverytime)

        Spacer(Modifier.weight(1f))

        OnDotButton(
            buttonType = if (input.isBlank()) ButtonType.Gray300 else ButtonType.Green500,
            buttonText = WORD_NEXT,
            onClick = { onNext(input) },
            modifier =
                Modifier
                    .buttonPadding(),
        )
    }
}

@Composable
private fun UrlInputTextField(
    input: String,
    onValueChange: (String) -> Unit,
) {
    RoundedTextField(
        value = input,
        onValueChange = onValueChange,
        placeholder = PASTE_URL,
        maxLength = 100,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        trailingIcon = {
            if (input.isNotBlank()) {
                Image(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Gray400),
                    modifier =
                        Modifier
                            .size(20.dp)
                            .noRippleClickable { onValueChange("") },
                )
            }
        },
    )
}

@Composable
private fun EverytimeOpenTextButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(start = 4.dp),
        modifier = Modifier.wrapContentWidth(),
    ) {
        OnDotText(
            text = "에브리타임 열기",
            style = OnDotTextStyle.BodyMediumM,
            color = Gray400,
            textDecoration = TextDecoration.Underline,
        )
    }
}
