package com.ondot.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.EMPTY_SCHEDULE
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray500
import com.ondot.design_system.components.OnDotText
import com.ondot.domain.model.enums.OnDotTextStyle
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_guide_arrow
import ondot.core.design_system.generated.resources.ic_no_clock
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmptyScheduleContent() {

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_no_clock),
                contentDescription = null,
                modifier = Modifier.size(width = 84.dp, height = 94.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OnDotText(
                text = EMPTY_SCHEDULE,
                style = OnDotTextStyle.TitleSmallM,
                color = Gray500
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            Image(
                painter = painterResource(Res.drawable.ic_guide_arrow),
                contentDescription = null,
                modifier = Modifier.size(100.dp, 140.dp).padding(end = 8.dp)
            )

            Spacer(Modifier.height(36.dp))
        }
    }
}