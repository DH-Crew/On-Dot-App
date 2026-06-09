package com.ondot.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ondot.ui.screen.loading.RouteLoadingScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditRouteLoadingRoute(
    viewModel: EditScheduleViewModel = koinViewModel(),
    navigateToEdit: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isAlarmRecalculating) {
        if (!uiState.isAlarmRecalculating) {
            navigateToEdit()
        }
    }

    RouteLoadingScreen(
        autoNavigate = false,
        navigateToNext = {},
    )
}
