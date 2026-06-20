package com.ondot.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.ONBOARDING2_PLACEHOLDER
import com.dh.ondot.presentation.ui.theme.ONBOARDING2_SUB_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING2_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING2_TITLE_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.OnDotTheme
import com.dh.ondot.presentation.ui.theme.WORD_NEXT
import com.ondot.designsystem.components.AddressList
import com.ondot.designsystem.components.OnDotButton
import com.ondot.designsystem.components.OnDotHighlightText
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.components.RoundedTextField
import com.ondot.designsystem.components.StepProgressIndicator
import com.ondot.designsystem.components.topbar.CommonTopBar
import com.ondot.designsystem.components.topbar.model.TopBarStyle
import com.ondot.designsystem.theme.OnDotColor.Gray300
import com.ondot.designsystem.theme.OnDotColor.Gray400
import com.ondot.designsystem.theme.OnDotColor.Gray800
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.member.AddressInfo
import com.ondot.onboarding.contract.OnboardingIntent
import com.ondot.onboarding.contract.OnboardingViewModel
import com.ondot.ui.platform
import com.ondot.ui.util.buttonPadding
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_search
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeAddressRoute(
    viewModel: OnboardingViewModel = koinViewModel(),
    navigateToAlarmSound: () -> Unit,
    navigateToMapProvider: () -> Unit,
    popScreen: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.dispatch(
            OnboardingIntent.InitStep(
                currentStep = 2,
                totalStep = if (platform() == ANDROID) 4 else 3,
            ),
        )
    }

    HomeAddressScreen(
        totalStep = uiState.totalStep,
        currentStep = uiState.currentStep,
        addressInput = uiState.addressInput,
        enabled = uiState.addressInputEnabled,
        placeList = uiState.placeList,
        onInputChanged = { viewModel.dispatch(OnboardingIntent.SearchPlace(it)) },
        onAddressSelected = { viewModel.dispatch(OnboardingIntent.SetHomeAddress(it)) },
        onBack = popScreen,
        onNext = {
            if (platform() == ANDROID) {
                navigateToAlarmSound()
            } else {
                navigateToMapProvider()
            }
        },
    )
}

@Composable
private fun HomeAddressScreen(
    totalStep: Int = 2,
    currentStep: Int = 1,
    addressInput: String = "",
    enabled: Boolean = false,
    placeList: List<AddressInfo> = emptyList(),
    onInputChanged: (String) -> Unit = {},
    onAddressSelected: (AddressInfo) -> Unit = {},
    onBack: () -> Unit = {},
    onNext: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Gray900),
    ) {
        Column(
            modifier =
                Modifier
                    .buttonPadding(),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 22.dp),
            ) {
                CommonTopBar(
                    style = TopBarStyle.BackCenterTitle(""),
                    onClick = onBack,
                )

                Spacer(Modifier.height(24.dp))

                StepProgressIndicator(
                    totalStep = totalStep,
                    currentStep = currentStep,
                )

                Spacer(Modifier.height(32.dp))

                OnDotHighlightText(
                    text = ONBOARDING2_TITLE,
                    highlight = ONBOARDING2_TITLE_HIGHLIGHT,
                    style = OnDotTextStyle.TitleMediumM,
                )

                Spacer(Modifier.height(8.dp))

                OnDotText(
                    text = ONBOARDING2_SUB_TITLE,
                    style = OnDotTextStyle.BodyMediumR,
                    color = Gray300,
                )

                Spacer(Modifier.height(40.dp))

                RoundedTextField(
                    value = addressInput,
                    onValueChange = onInputChanged,
                    maxLength = 50,
                    placeholder = ONBOARDING2_PLACEHOLDER,
                    keyboardOptions =
                        KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                        ),
                    leadingIcon = {
                        Image(
                            painter = painterResource(Res.drawable.ic_search),
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .size(20.dp),
                            colorFilter = ColorFilter.tint(color = Gray400),
                        )
                    },
                )

                Spacer(Modifier.height(24.dp))
            }

            HorizontalDivider(thickness = 8.dp, color = Gray800)

            AddressList(
                modifier = Modifier.weight(1f),
                query = addressInput,
                addressList = placeList,
                onClick = {
                    onAddressSelected(it)
                    focusManager.clearFocus()
                },
            )

            Spacer(Modifier.height(16.dp))

            OnDotButton(
                modifier =
                    Modifier
                        .padding(horizontal = 22.dp),
                buttonText = WORD_NEXT,
                buttonType = if (enabled) ButtonType.Green500 else ButtonType.Gray300,
                enabled = enabled,
                onClick = onNext,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    OnDotTheme {
        HomeAddressScreen()
    }
}
