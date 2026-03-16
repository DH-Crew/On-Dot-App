package com.ondot.designsystem.components.topbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.components.topbar.model.TopBarStyle
import com.ondot.designsystem.getPlatform
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.domain.model.enums.OnDotTextStyle
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_back
import ondot.core.design_system.generated.resources.ic_close
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun CommonTopBar(
    modifier: Modifier = Modifier,
    style: TopBarStyle,
    buttonColor: Color = Gray0,
    onClick: () -> Unit = {},
    content: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = if (getPlatform() == ANDROID) 50.dp else 70.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // leading
        when (style) {
            is TopBarStyle.CloseOnly,
            is TopBarStyle.CloseTitleEdit,
            -> {
                TopBarIconButton(
                    icon = Res.drawable.ic_close,
                    tint = buttonColor,
                    onClick = onClick,
                )
            }
            is TopBarStyle.BackCenterTitle -> {
                TopBarIconButton(
                    icon = Res.drawable.ic_back,
                    tint = buttonColor,
                    onClick = onClick,
                )
            }
        }

        // center
        when (style) {
            is TopBarStyle.CloseOnly -> {
                Spacer(Modifier.weight(1f))
            }

            is TopBarStyle.CloseTitleEdit -> {
                Spacer(Modifier.width(20.dp))
                content?.invoke()
            }

            is TopBarStyle.BackCenterTitle -> {
                OnDotText(
                    text = style.title,
                    style = OnDotTextStyle.TitleSmallM,
                    color = Gray0,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .weight(1f),
                )
                Spacer(Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun TopBarIconButton(
    icon: DrawableResource,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(tint),
        )
    }
}
