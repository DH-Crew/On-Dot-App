package com.ondot.design_system.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.ondot.design_system.getPlatform
import com.ondot.domain.model.enums.TopBarType
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_back
import ondot.core.design_system.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource

@Composable
fun TopBar(
    type: TopBarType = TopBarType.BACK,
    buttonColor: Color = Gray0,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(if (getPlatform() == ANDROID) 50.dp else 70.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(if (type == TopBarType.BACK) Res.drawable.ic_back else Res.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClick() },
                colorFilter = ColorFilter.tint(buttonColor)
            )

            Spacer(modifier = Modifier.width(20.dp))

            content()
        }
    }
}