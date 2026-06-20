package com.ondot.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray400
import com.ondot.designsystem.theme.OnDotColor.Green600
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.OnDotTextStyle
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_apple_map
import ondot.core.design_system.generated.resources.ic_kakao_map
import ondot.core.design_system.generated.resources.ic_naver_map
import org.jetbrains.compose.resources.painterResource

@Composable
fun MapProviderList(
    modifier: Modifier = Modifier,
    mapProviders: List<MapProvider>,
    selectedProvider: MapProvider,
    onProviderClick: (MapProvider) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        mapProviders.forEach { provider ->
            MapProviderItem(
                provider = provider,
                isSelected = selectedProvider == provider,
                modifier =
                    Modifier
                        .weight(1f)
                        .height(106.dp),
                onClick = { onProviderClick(provider) },
            )
        }
    }
}

@Composable
private fun MapProviderItem(
    provider: MapProvider,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val imageResource =
        when (provider) {
            MapProvider.KAKAO -> painterResource(Res.drawable.ic_kakao_map)
            MapProvider.NAVER -> painterResource(Res.drawable.ic_naver_map)
            MapProvider.APPLE -> painterResource(Res.drawable.ic_apple_map)
        }

    Box(
        modifier =
            modifier
                .background(Gray400, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, if (isSelected) Green600 else Gray400, RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ).padding(top = 14.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = imageResource,
                contentDescription = null,
                modifier = Modifier.size(46.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OnDotText(
                text = provider.providerName,
                style = OnDotTextStyle.BodyMediumR,
                color = Gray0,
            )
        }
    }
}
