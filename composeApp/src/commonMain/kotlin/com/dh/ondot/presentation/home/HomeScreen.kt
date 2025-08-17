package com.dh.ondot.presentation.home

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dh.ondot.domain.model.enums.MapProvider
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.home.components.AddScheduleButton
import com.dh.ondot.presentation.home.components.EmptyScheduleContent
import com.dh.ondot.presentation.home.components.RemainingTimeText
import com.dh.ondot.presentation.home.components.ScheduleList
import com.dh.ondot.presentation.home.components.UserBadgeBanner
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.CREATE_SCHEDULE_GUIDE
import com.dh.ondot.presentation.ui.theme.MAP_PROVIDER_CONTENT
import com.dh.ondot.presentation.ui.theme.MAP_PROVIDER_TITLE
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green600
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_apple_map
import ondot.composeapp.generated.resources.ic_banner
import ondot.composeapp.generated.resources.ic_kakao_map
import ondot.composeapp.generated.resources.ic_naver_map
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel { HomeViewModel() },
    navigateToGeneralSchedule: () -> Unit,
    navigateToEditSchedule: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.getScheduleList()
    }

    HomeContent(
        uiState = uiState,
        interactionSource = interactionSource,
        onToggle = { viewModel.onToggle() },
        onClickAlarmSwitch = { id, isEnabled -> viewModel.onClickAlarmSwitch(id, isEnabled) },
        onConfirmProvider = {
            viewModel.setMapProvider(it)
        },
        onDelete = { viewModel.deleteSchedule(it) },
        navigateToGeneralSchedule = navigateToGeneralSchedule,
        navigateToEditSchedule = navigateToEditSchedule
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    interactionSource: MutableInteractionSource,
    onToggle: () -> Unit,
    onClickAlarmSwitch: (Long, Boolean) -> Unit,
    onConfirmProvider: (MapProvider) -> Unit,
    onDelete: (Long) -> Unit,
    navigateToGeneralSchedule: () -> Unit,
    navigateToEditSchedule: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
        ) {
            Spacer(modifier = Modifier.height(33.dp))

            UserBadgeBanner()

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.remainingTime.first != -1) {
                RemainingTimeText(
                    day = uiState.remainingTime.first,
                    hour = uiState.remainingTime.second,
                    minute = uiState.remainingTime.third
                )
            } else {
                OnDotText(text = CREATE_SCHEDULE_GUIDE, style = OnDotTextStyle.TitleMediumSB, color = Gray0)
            }

            Spacer(modifier = Modifier.height(36.dp))

            if (uiState.scheduleList.isEmpty()) {
                Image(
                    painter = painterResource(Res.drawable.ic_banner),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(349f/100f)
                )

                Spacer(modifier = Modifier.height(120.dp))

                EmptyScheduleContent()
            } else {
                ScheduleList(
                    scheduleList = uiState.scheduleList,
                    interactionSource = interactionSource,
                    onClickSwitch = onClickAlarmSwitch,
                    onClickSchedule = navigateToEditSchedule,
                    onDelete = onDelete
                )
            }
        }

        AddScheduleButton(
            isExpanded = uiState.isExpanded,
            interactionSource = interactionSource,
            onToggle = onToggle,
            onQuickAdd = { /*TODO*/ },
            onGeneralAdd = navigateToGeneralSchedule
        )

        if (uiState.needsChooseProvider) {
            MapProviderDialog(
                mapProviders = uiState.mapProviders,
                interactionSource = interactionSource,
                onConfirmProvider = onConfirmProvider
            )
        }
    }
}

@Composable
private fun MapProviderDialog(
    mapProviders: List<MapProvider>,
    interactionSource: MutableInteractionSource,
    onConfirmProvider: (MapProvider) -> Unit
) {
    val selectedIndex by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900.copy(alpha = 0.8f))
            .padding(horizontal = 52.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Gray600, RoundedCornerShape(12.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnDotText(
                text = MAP_PROVIDER_TITLE,
                style = OnDotTextStyle.TitleSmallM,
                color = Gray0,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            OnDotText(
                text = MAP_PROVIDER_CONTENT,
                style = OnDotTextStyle.BodyMediumR,
                color = Gray0
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                mapProviders.forEachIndexed { index, provider ->
                    MapProviderItem(
                        provider = provider,
                        isSelected = index == selectedIndex,
                        modifier = Modifier.padding(bottom = 12.dp),
                        interactionSource = interactionSource,
                        onClick = {
                            onConfirmProvider(provider)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MapProviderItem(
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