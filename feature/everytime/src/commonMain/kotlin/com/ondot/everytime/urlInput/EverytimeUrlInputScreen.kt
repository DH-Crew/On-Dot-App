package com.ondot.everytime.urlInput

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.LANDING_SCREEN_TITLE
import com.dh.ondot.presentation.ui.theme.OPEN_EVERYTIME
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
import com.ondot.designsystem.theme.OnDotColor.Gray400
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.designsystem.theme.OnDotColor.Green700
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.service.ClipboardReader
import com.ondot.domain.service.ExternalAppLauncher
import com.ondot.everytime.component.EverytimeGuidePager
import com.ondot.everytime.contract.EverytimeIntent
import com.ondot.everytime.contract.EverytimeSideEffect
import com.ondot.everytime.contract.EverytimeViewModel
import com.ondot.ui.util.ToastManager
import com.ondot.ui.util.buttonPadding
import com.ondot.ui.util.noRippleClickable
import kotlinx.coroutines.launch
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_close
import ondot.core.design_system.generated.resources.ic_open_url
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EverytimeUrlInputRoute(
    viewModel: EverytimeViewModel = koinViewModel(),
    appLauncher: ExternalAppLauncher = koinInject(),
    clipboardReader: ClipboardReader = koinInject(),
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
        readClipboardText = { clipboardReader.readText() },
    )
}

@Composable
private fun EverytimeUrlInputScreen(
    onBack: () -> Unit,
    onOpenEverytime: () -> Unit,
    onNext: (String) -> Unit,
    readClipboardText: suspend () -> String?,
) {
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

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

        Spacer(Modifier.height(28.dp))

        EverytimeGuidePager(
            modifier = Modifier.weight(1f),
        )

        Spacer(Modifier.height(36.dp))

        EverytimeOpenTextButton(onClick = onOpenEverytime)

        Spacer(Modifier.height(12.dp))

        UrlInputTextField(
            input = input,
            onValueChange = { input = it },
            onFocused = {
                if (input.isBlank()) {
                    scope.launch {
                        val clipboardText = readClipboardText()
                        if (!clipboardText.isNullOrBlank()) {
                            input = clipboardText
                        }
                    }
                }
            },
        )

        Spacer(Modifier.height(44.dp))

        OnDotButton(
            buttonType = if (input.isBlank()) ButtonType.Gray300 else ButtonType.Green500,
            buttonText = WORD_NEXT,
            onClick = { onNext(input) },
            modifier =
                Modifier
                    .buttonPadding()
                    .imePadding(),
        )
    }
}

@Composable
private fun UrlInputTextField(
    input: String,
    onValueChange: (String) -> Unit,
    onFocused: () -> Unit,
) {
    RoundedTextField(
        value = input,
        onValueChange = onValueChange,
        placeholder = URL_INPUT_GUIDE,
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
        onFocused = onFocused,
    )
}

@Composable
private fun EverytimeOpenTextButton(onClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .noRippleClickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        OnDotText(
            text = OPEN_EVERYTIME,
            style = OnDotTextStyle.BodyMediumR,
            color = Green700,
        )

        Image(
            painter = painterResource(Res.drawable.ic_open_url),
            contentDescription = null,
            modifier =
                Modifier
                    .size(20.dp),
        )
    }
}
