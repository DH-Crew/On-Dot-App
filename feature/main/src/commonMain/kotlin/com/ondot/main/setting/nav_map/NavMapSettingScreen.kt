package com.ondot.main.setting.nav_map

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green300
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green600
import com.dh.ondot.presentation.ui.theme.SETTING_NAV_MAP
import com.dh.ondot.presentation.ui.theme.SETTING_NAV_MAP_PROVIDER_GUIDE
import com.dh.ondot.presentation.ui.theme.SETTING_NAV_MAP_PROVIDER_TITLE
import com.dh.ondot.presentation.ui.theme.WORD_SAVE
import com.ondot.design_system.components.OnDotButton
import com.ondot.design_system.components.OnDotText
import com.ondot.design_system.components.TopBar
import com.ondot.design_system.getPlatform
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.TopBarType
import com.ondot.main.setting.SettingEvent
import com.ondot.main.setting.SettingViewModel
import com.ondot.util.AnalyticsLogger
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_apple_map
import ondot.core.design_system.generated.resources.ic_kakao_map
import ondot.core.design_system.generated.resources.ic_naver_map
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavMapSettingScreen(
    popScreen: () -> Unit,
    viewModel: SettingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        AnalyticsLogger.logEvent("screen_view_nav_map_setting")
    }

    LaunchedEffect(Unit) {
        viewModel.getUserMapProvider()
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect {
            when (it) {
                is SettingEvent.PopScreen -> popScreen()
            }
        }
    }

    NavMapSettingContent(
        mapProviders = uiState.mapProviders,
        selectedProvider = uiState.selectedProvider,
        interactionSource = interactionSource,
        onBack = popScreen,
        onProviderClick = viewModel::updateSelectedProvider,
        onSaveClick = viewModel::updateMapProvider
    )
}

@Composable
fun NavMapSettingContent(
    mapProviders: List<MapProvider>,
    selectedProvider: MapProvider,
    interactionSource: MutableInteractionSource,
    onBack: () -> Unit,
    onProviderClick: (MapProvider) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900)
            .padding(horizontal = 22.dp)
    ) {
        TopBarSection(
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(32.dp))

        OnDotText(
            text = SETTING_NAV_MAP_PROVIDER_TITLE,
            style = OnDotTextStyle.TitleMediumM,
            textAlign = TextAlign.Start,
            color = Gray0
        )

        Spacer(modifier = Modifier.height(16.dp))

        OnDotText(text = SETTING_NAV_MAP_PROVIDER_GUIDE, style = OnDotTextStyle.BodyMediumR, color = Green300)

        Spacer(modifier = Modifier.height(40.dp))

        MapProviderList(
            modifier = Modifier.weight(1f),
            mapProviders = mapProviders,
            selectedProvider = selectedProvider,
            interactionSource = interactionSource,
            onProviderClick = onProviderClick
        )

        OnDotButton(
            modifier = Modifier.padding(bottom = if (getPlatform() == ANDROID) 16.dp else 37.dp),
            buttonText = WORD_SAVE,
            buttonType = ButtonType.Green500,
            onClick = onSaveClick
        )
    }
}

@Composable
private fun TopBarSection(
    onBack: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TopBar(
            type = TopBarType.BACK,
            onClick = onBack
        )

        OnDotText(
            text = SETTING_NAV_MAP,
            style = OnDotTextStyle.TitleSmallM,
            color = Gray0,
            modifier = Modifier.padding(top = if (getPlatform() == ANDROID) 50.dp else 70.dp)
        )
    }
}

@Composable
private fun MapProviderList(
    modifier: Modifier = Modifier,
    mapProviders: List<MapProvider>,
    selectedProvider: MapProvider,
    interactionSource: MutableInteractionSource,
    onProviderClick: (MapProvider) -> Unit
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        mapProviders.forEachIndexed { index, provider ->
            MapProviderItem(
                provider = provider,
                isSelected = selectedProvider == provider,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 12.dp),
                aspectRatio = 105f / 128f,
                interactionSource = interactionSource,
                onClick = { onProviderClick(provider) }
            )
        }
    }
}

@Composable
private fun MapProviderItem(
    provider: MapProvider,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    aspectRatio: Float,
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
            .aspectRatio(aspectRatio)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = imageResource,
                contentDescription = null,
                modifier = Modifier.aspectRatio(1f)
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