package com.dh.ondot.presentation.onboarding.step

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.components.OnDotHighlightText
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.PlaceSearchResultItem
import com.dh.ondot.presentation.ui.components.RoundedTextField
import com.dh.ondot.presentation.ui.theme.ONBOARDING2_PLACEHOLDER
import com.dh.ondot.presentation.ui.theme.ONBOARDING2_SUB_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING2_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING2_TITLE_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.member.AddressInfo
import com.ondot.util.AnalyticsLogger
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_search
import org.jetbrains.compose.resources.painterResource

@Composable
fun OnboardingStep2(
    addressInput: String,
    onAddressInputChanged: (String) -> Unit,
    addressList: List<AddressInfo>,
    onClickPlace: (AddressInfo) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        AnalyticsLogger.logEvent("screen_view_onboarding_step_2")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp)
        ) {
            OnDotHighlightText(
                text = ONBOARDING2_TITLE,
                highlight = ONBOARDING2_TITLE_HIGHLIGHT,
                style = OnDotTextStyle.TitleMediumM,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))

            OnDotText(
                text = ONBOARDING2_SUB_TITLE,
                style = OnDotTextStyle.BodyMediumR,
                color = OnDotColor.Green300
            )

            Spacer(modifier = Modifier.height(40.dp))

            RoundedTextField(
                value = addressInput,
                onValueChange = onAddressInputChanged,
                placeholder = ONBOARDING2_PLACEHOLDER,
                modifier = Modifier.fillMaxWidth(),
                maxLength = 100,
                leadingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(color = OnDotColor.Gray800)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {
            itemsIndexed(addressList) { index, item ->
                Column(
                    modifier = Modifier
                        .clickable {
                            onClickPlace(addressList[index])
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                ) {
                    PlaceSearchResultItem(
                        addressInput = addressInput,
                        item = item
                    )

                    if (index < addressList.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp),
                            color = OnDotColor.Gray800
                        )
                    }
                }
            }
        }
    }
}