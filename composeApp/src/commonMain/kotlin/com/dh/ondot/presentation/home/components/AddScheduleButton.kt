package com.dh.ondot.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.enums.ScheduleMenuType
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.ADD_SCHEDULE
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray800
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_clock_white
import ondot.composeapp.generated.resources.ic_plus
import ondot.composeapp.generated.resources.ic_quick
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddScheduleButton(
    isExpanded: Boolean,
    interactionSource: MutableInteractionSource,
    onToggle: () -> Unit,
    onQuickAdd: () -> Unit,
    onGeneralAdd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Gray900.copy(alpha = 0.8f))
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = { onToggle() }
                    )
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 22.dp),
            horizontalAlignment = Alignment.End
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AddScheduleMenu(
                    onQuickAdd = {
                        onToggle()
                        onQuickAdd()
                    },
                    onGeneralAdd = {
                        onToggle()
                        onGeneralAdd()
                    }
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            AddScheduleFloatingButton(
                isExpanded = isExpanded,
                onToggle = onToggle
            )
        }
    }
}

@Composable
private fun AddScheduleMenu(
    onQuickAdd: () -> Unit,
    onGeneralAdd: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Gray600, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
//        AddScheduleMenuItem(type = ScheduleMenuType.Quick, onClick = onQuickAdd)
        AddScheduleMenuItem(type = ScheduleMenuType.General, onClick = onGeneralAdd)
    }
}

@Composable
private fun AddScheduleMenuItem(
    type: ScheduleMenuType,
    onClick: () -> Unit
) {
    val resourceId = when(type) {
        ScheduleMenuType.Quick -> Res.drawable.ic_quick
        ScheduleMenuType.General -> Res.drawable.ic_clock_white
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(start = 8.dp, end = 19.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(resourceId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        OnDotText(
            text = type.text,
            style = OnDotTextStyle.BodyLargeR1,
            color = Gray0
        )
    }
}

@Composable
fun AddScheduleFloatingButton(
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val transition = updateTransition(isExpanded, label = "fabTransition")
    val width by transition.animateDp(
        transitionSpec = { tween(150) },
        label = "fabWidth"
    ) { expanded ->
        if (expanded) 40.dp else 106.dp
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Green500)
            .clickable { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_plus),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            if (!isExpanded) {
                Spacer(Modifier.width(6.dp))
                OnDotText(
                    text = ADD_SCHEDULE,
                    style = OnDotTextStyle.BodyLargeSB,
                    color = Gray800
                )
            }
        }
    }
}
