package com.dh.ondot.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.response.AddressInfo
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_search
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlaceSearchResultItem(
    addressInput: String,
    item: AddressInfo
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            OnDotHighlightText(
                text = item.title,
                textColor = OnDotColor.Gray0,
                highlight = addressInput,
                highlightColor = OnDotColor.Green600,
                style = OnDotTextStyle.BodyLargeR1,
            )

            Spacer(modifier = Modifier.height(4.dp))

            OnDotText(
                text = item.roadAddress,
                style = OnDotTextStyle.BodyLargeR2,
                color = OnDotColor.Gray300
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}