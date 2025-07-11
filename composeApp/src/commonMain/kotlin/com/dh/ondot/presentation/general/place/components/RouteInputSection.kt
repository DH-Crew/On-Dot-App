package com.dh.ondot.presentation.general.place.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.enums.RouterType
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.ARRIVAL_INPUT_PLACEHOLDER
import com.dh.ondot.presentation.ui.theme.DEPARTURE_INPUT_PLACEHOLDER
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray300
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Red
import com.dh.ondot.presentation.ui.theme.OnDotTypo

@Composable
fun RouteInputSection(
    departurePlaceInput: String,
    arrivalPlaceInput: String,
    departureFocusRequester: FocusRequester,
    arrivalFocusRequester: FocusRequester,
    onRouteInputChanged: (String) -> Unit,
    onRouteInputFocused: (RouterType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = Gray600, shape = RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp)
    ) {
        RouteInputTextField(
            type = RouterType.Departure,
            input = departurePlaceInput,
            focusRequester = departureFocusRequester,
            onValueChanged = onRouteInputChanged,
            onRouteInputFocused =  onRouteInputFocused
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(thickness = (0.5).dp, color = Gray600)

        Spacer(modifier = Modifier.height(16.dp))

        RouteInputTextField(
            type = RouterType.Arrival,
            input = arrivalPlaceInput,
            focusRequester = arrivalFocusRequester,
            onValueChanged = onRouteInputChanged,
            onRouteInputFocused =  onRouteInputFocused
        )
    }
}

@Composable
fun RouteInputTextField(
    type: RouterType,
    input: String,
    maxLength: Int = 200,
    focusRequester: FocusRequester,
    onValueChanged: (String) -> Unit,
    onRouteInputFocused: (RouterType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .padding(3.dp)
                .background(
                    color = if (type == RouterType.Departure) Red.copy(alpha = 0.5f) else Green600.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color = if (type == RouterType.Departure) Red else Green600, shape = CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        OnDotText(
            text = if (type == RouterType.Departure) DEPARTURE_INPUT_PLACEHOLDER else ARRIVAL_INPUT_PLACEHOLDER,
            style = OnDotTextStyle.BodyLargeR1,
            color = if (input.isEmpty()) Gray300 else Gray0
        )

        BasicTextField(
            value = input,
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChanged(it)
                }
            },
            singleLine = true,
            textStyle = OnDotTypo().bodyLargeR1.copy(color = Gray0),
            cursorBrush = SolidColor(Gray0),
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused) onRouteInputFocused(type)
                }
        )
    }
}