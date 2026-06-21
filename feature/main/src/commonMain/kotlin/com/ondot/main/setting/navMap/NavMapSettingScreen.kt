package com.ondot.main.setting.navMap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.SETTING_NAV_MAP
import com.dh.ondot.presentation.ui.theme.SETTING_NAV_MAP_PROVIDER_GUIDE
import com.dh.ondot.presentation.ui.theme.SETTING_NAV_MAP_PROVIDER_TITLE
import com.dh.ondot.presentation.ui.theme.WORD_SAVE
import com.ondot.designsystem.components.MapProviderList
import com.ondot.designsystem.components.OnDotButton
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.components.TopBar
import com.ondot.designsystem.getPlatform
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.designsystem.theme.OnDotColor.Green300
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.TopBarType
import com.ondot.main.setting.SettingEvent
import com.ondot.main.setting.SettingViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavMapSettingScreen(
    popScreen: () -> Unit,
    viewModel: SettingViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        onBack = popScreen,
        onProviderClick = viewModel::updateSelectedProvider,
        onSaveClick = viewModel::updateMapProvider,
    )
}

@Composable
fun NavMapSettingContent(
    mapProviders: List<MapProvider>,
    selectedProvider: MapProvider,
    onBack: () -> Unit,
    onProviderClick: (MapProvider) -> Unit,
    onSaveClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Gray900)
                .padding(horizontal = 22.dp),
    ) {
        TopBarSection(
            onBack = onBack,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OnDotText(
            text = SETTING_NAV_MAP_PROVIDER_TITLE,
            style = OnDotTextStyle.TitleMediumM,
            textAlign = TextAlign.Start,
            color = Gray0,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OnDotText(text = SETTING_NAV_MAP_PROVIDER_GUIDE, style = OnDotTextStyle.BodyMediumR, color = Green300)

        Spacer(modifier = Modifier.height(40.dp))

        MapProviderList(
            modifier = Modifier.weight(1f),
            mapProviders = mapProviders,
            selectedProvider = selectedProvider,
            onProviderClick = onProviderClick,
        )

        OnDotButton(
            modifier = Modifier.padding(bottom = if (getPlatform() == ANDROID) 16.dp else 37.dp),
            buttonText = WORD_SAVE,
            buttonType = ButtonType.Green500,
            onClick = onSaveClick,
        )
    }
}

@Composable
private fun TopBarSection(onBack: () -> Unit = {}) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        TopBar(
            type = TopBarType.BACK,
            onClick = onBack,
        )

        OnDotText(
            text = SETTING_NAV_MAP,
            style = OnDotTextStyle.TitleSmallM,
            color = Gray0,
            modifier = Modifier.padding(top = if (getPlatform() == ANDROID) 50.dp else 70.dp),
        )
    }
}
