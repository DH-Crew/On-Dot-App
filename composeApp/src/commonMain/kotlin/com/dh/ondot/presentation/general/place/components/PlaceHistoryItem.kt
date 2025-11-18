package com.dh.ondot.presentation.general.place.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.general.GeneralScheduleUiState
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray300
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray50
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.member.PlaceHistory
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlaceHistoryItem(
    item: PlaceHistory,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OnDotText(
            text = item.title,
            style = OnDotTextStyle.BodyLargeR1,
            color = Gray50
        )

        Spacer(modifier = Modifier.weight(1f))

        OnDotText(
            text = GeneralScheduleUiState.formattedDate(item.searchedAt),
            style = OnDotTextStyle.BodyMediumR,
            color = Gray300
        )

        Spacer(Modifier.width(8.dp))

        Image(
            painter = painterResource(Res.drawable.ic_close),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = Gray400),
            modifier = Modifier.size(12.dp).clickable{ onClick() }
        )
    }
}