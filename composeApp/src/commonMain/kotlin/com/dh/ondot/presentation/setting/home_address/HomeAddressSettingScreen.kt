package com.dh.ondot.presentation.setting.home_address

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.setting.SettingViewModel
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray200
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.SETTING_HOME_ADDRESS
import com.dh.ondot.presentation.ui.theme.WORD_HOME
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.TopBarType
import com.ondot.domain.model.member.HomeAddressInfo
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_home_circle_green
import ondot.composeapp.generated.resources.ic_pencil_white
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeAddressSettingScreen(
    popScreen: () -> Unit,
    navigateToHomeAddressEditScreen: () -> Unit,
    viewModel: SettingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        if (uiState.homeAddress.roadAddress.isEmpty()) viewModel.getHomeAddress()
    }

    HomeAddressSettingContent(
        homeAddress = uiState.homeAddress,
        interactionSource = interactionSource,
        onBackClick = popScreen,
        onHomeAddressClick = navigateToHomeAddressEditScreen
    )
}

@Composable
fun HomeAddressSettingContent(
    homeAddress: HomeAddressInfo = HomeAddressInfo(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onBackClick: () -> Unit = {},
    onHomeAddressClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900)
            .padding(horizontal = 22.dp)
    ) {
        TopBarSection(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(32.dp))

        HomeAddressInfoItem(
            homeAddress = homeAddress,
            interactionSource = interactionSource,
            onClick = onHomeAddressClick
        )
    }
}

@Composable
private fun TopBarSection(
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TopBar(
            type = TopBarType.BACK,
            onClick = onBackClick
        )

        OnDotText(
            text = SETTING_HOME_ADDRESS,
            style = OnDotTextStyle.TitleSmallM,
            color = Gray0,
            modifier = Modifier.padding(top = if (getPlatform().name == ANDROID) 50.dp else 70.dp)
        )
    }
}

@Composable
private fun HomeAddressInfoItem(
    homeAddress: HomeAddressInfo,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_home_circle_green),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            OnDotText(text = WORD_HOME, style = OnDotTextStyle.BodyMediumM, color = Gray200)

            Spacer(modifier = Modifier.height(4.dp))

            OnDotText(text = homeAddress.roadAddress, style = OnDotTextStyle.BodyLargeR1, color = Gray0)

            Spacer(modifier = Modifier.height(4.dp))

            OnDotText(text = homeAddress.roadAddress, style = OnDotTextStyle.BodyMediumM, color = Gray200)
        }

        Image(
            painter = painterResource(Res.drawable.ic_pencil_white),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(Gray400)
        )
    }
}