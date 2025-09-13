package com.dh.ondot.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray500
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green600
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SchedulePreparationItem(
    content: String,
    resourceId: DrawableResource
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray500, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(resource = resourceId),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        OnDotText(
            text = content,
            style = OnDotTextStyle.BodyLargeSB,
            color = Green600
        )
    }
}