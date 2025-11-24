package com.ondot.design_system.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green600
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.OnDotTextStyle
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_apple_map
import ondot.core.design_system.generated.resources.ic_kakao_map
import ondot.core.design_system.generated.resources.ic_naver_map
import org.jetbrains.compose.resources.painterResource

@Composable
fun MapProviderItem(
    provider: MapProvider,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
) {
    val imageResource = when(provider) {
        MapProvider.KAKAO -> painterResource(Res.drawable.ic_kakao_map)
        MapProvider.NAVER -> painterResource(Res.drawable.ic_naver_map)
        MapProvider.APPLE -> painterResource(Res.drawable.ic_apple_map)
    }

    Box(
        modifier = modifier
            .background(Gray400, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, if (isSelected) Green600 else Gray400, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = imageResource,
                contentDescription = null,
                modifier = Modifier.size(46.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OnDotText(
                text = provider.providerName,
                style = OnDotTextStyle.BodyMediumR,
                color = Gray0
            )
        }
    }
}