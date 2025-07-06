package com.dh.ondot.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dh.ondot.domain.model.enums.BottomNavType
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray500
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray800
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_home_selected
import ondot.composeapp.generated.resources.ic_home_unselected
import ondot.composeapp.generated.resources.ic_settings_selected
import ondot.composeapp.generated.resources.ic_settings_unselected
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel { MainViewModel() }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                type = uiState.bottomNavType,
                interactionSource = interactionSource,
                onClick = { viewModel.setBottomNavType(it) }
            )
        }
    ) {
        // TODO(HomeScreen, SettingScreen을 분기처리하는 로직 구현)

        Box(
            modifier = Modifier.fillMaxSize().background(color = OnDotColor.Gray900),
            contentAlignment = Alignment.Center
        ) {
            OnDotText("임시 화면", style = OnDotTextStyle.BodyLargeSB, color = OnDotColor.Gray0)
        }
    }
}

@Composable
fun BottomNavBar(
    type: BottomNavType = BottomNavType.HOME,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
    onClick: (String) -> Unit = {},
) {
    Column {
        HorizontalDivider(color = Gray500, thickness = 1.dp)

        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(Gray800.copy(alpha = 0.8f)),
            horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            BottomNavItem(
                resourceId = if (type == BottomNavType.HOME) Res.drawable.ic_home_selected else Res.drawable.ic_home_unselected,
                type = BottomNavType.HOME,
                interactionSource = interactionSource,
                onClick = onClick
            )

            BottomNavItem(
                resourceId = if (type == BottomNavType.SETTING) Res.drawable.ic_settings_selected else Res.drawable.ic_settings_unselected,
                type = BottomNavType.SETTING,
                interactionSource = interactionSource,
                onClick = onClick
            )
        }
    }
}

@Composable
fun BottomNavItem(
    resourceId: DrawableResource,
    type: BottomNavType = BottomNavType.DEFAULT,
    interactionSource: MutableInteractionSource,
    onClick: (String) -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .size(width = 40.dp, height = 52.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    when (type) {
                        BottomNavType.HOME -> {
//                            onClick(NavRoutes.HomeScreen.route)
                        }
                        BottomNavType.SETTING -> {
//                            onClick(NavRoutes.SettingScreen.route)
                        }
                        BottomNavType.DEFAULT -> return@clickable
                    }
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(resourceId),
            contentDescription = null,
            modifier = Modifier.size(width = 40.dp, height = 52.dp)
        )
    }
}