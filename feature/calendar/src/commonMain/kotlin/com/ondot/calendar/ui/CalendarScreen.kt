package com.ondot.calendar.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ondot.calendar.contract.CalendarIntent
import com.ondot.calendar.contract.CalendarInteractionState
import com.ondot.calendar.contract.CalendarSheetAnchor
import com.ondot.calendar.contract.CalendarSheetState
import com.ondot.calendar.contract.CalendarUiState
import com.ondot.calendar.contract.CalendarViewModel
import com.ondot.calendar.contract.GestureAxis
import com.ondot.calendar.contract.MonthTransitionDirection
import com.ondot.calendar.contract.rememberCalendarSheetState
import com.ondot.calendar.contract.toCalendarCells
import com.ondot.calendar.ui.component.CalendarBottomSheet
import com.ondot.calendar.ui.component.CalendarMonthGrid
import com.ondot.calendar.ui.component.CalendarTopBar
import com.ondot.calendar.ui.component.CalendarWeekHeader
import com.ondot.designsystem.theme.OnDotColor.Gray900
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CalendarRoute(viewModel: CalendarViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CalendarScreen(
        uiState = uiState,
        onSelectDate = { viewModel.dispatch(CalendarIntent.SelectDate(it)) },
        onMoveToPreviousMonth = { viewModel.dispatch(CalendarIntent.MoveToPreviousMonth) },
        onMoveToNextMonth = { viewModel.dispatch(CalendarIntent.MoveToNextMonth) },
    )
}

@Composable
private fun CalendarScreen(
    uiState: CalendarUiState,
    onSelectDate: (LocalDate) -> Unit,
    onMoveToPreviousMonth: () -> Unit,
    onMoveToNextMonth: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberCalendarSheetState()

    var containerHeightPx by remember { mutableFloatStateOf(0f) }
    var containerWidthPx by remember { mutableFloatStateOf(0f) }

    val interactionState =
        rememberCalendarInteractionState(
            uiState = uiState,
            sheetState = sheetState,
            onSelectDate = onSelectDate,
            onMoveToPreviousMonth = onMoveToPreviousMonth,
            onMoveToNextMonth = onMoveToNextMonth,
            onShowPeekSheet = {
                scope.launch {
                    sheetState.animateTo(CalendarSheetAnchor.Peek)
                }
            },
        )

    val sheetWeight = sheetState.currentWeight
    val hiddenToPeek = sheetState.hiddenToPeekProgress
    val peekToExpanded = sheetState.peekToExpandedProgress

    val eventLabelAlpha = 1f - hiddenToPeek
    val dimAlpha = peekToExpanded * 0.08f

    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Gray900)
                .onSizeChanged { size ->
                    containerHeightPx = size.height.toFloat()
                    containerWidthPx = size.width.toFloat()
                }.pointerInput(uiState.currentMonth, containerHeightPx, containerWidthPx) {
                    var totalDragX = 0f
                    var totalDragY = 0f
                    var gestureAxis: GestureAxis? = null

                    detectDragGestures(
                        onDragStart = {
                            totalDragX = 0f
                            totalDragY = 0f
                            gestureAxis = null
                        },
                        onDrag = { change, dragAmount ->
                            totalDragX += dragAmount.x
                            totalDragY += dragAmount.y

                            if (gestureAxis == null) {
                                val absX = kotlin.math.abs(totalDragX)
                                val absY = kotlin.math.abs(totalDragY)

                                if (absX > 8f || absY > 8f) {
                                    gestureAxis =
                                        if (absX > absY) {
                                            GestureAxis.Horizontal
                                        } else {
                                            GestureAxis.Vertical
                                        }
                                }
                            }

                            when (gestureAxis) {
                                GestureAxis.Horizontal -> {
                                    change.consume()
                                }

                                GestureAxis.Vertical -> {
                                    change.consume()

                                    if (containerHeightPx == 0f) return@detectDragGestures

                                    val deltaWeight = -dragAmount.y / containerHeightPx
                                    sheetState.snapBy(deltaWeight)
                                }

                                null -> Unit
                            }
                        },
                        onDragEnd = {
                            when (gestureAxis) {
                                GestureAxis.Horizontal -> {
                                    interactionState.handleHorizontalSwipe(
                                        totalDragX = totalDragX,
                                        containerWidthPx = containerWidthPx,
                                    )
                                }

                                GestureAxis.Vertical -> {
                                    scope.launch {
                                        sheetState.animateTo(sheetState.resolveTarget())
                                    }
                                }

                                null -> Unit
                            }
                        },
                    )
                },
    ) {
        val fullHeight = maxHeight

        /**
         * 0.0 ~ 0.4
         * calendar: 1.0 -> 0.6
         * baseSheet: 0.0 -> 0.4
         *
         * 0.4 ~ 1.0
         * calendar: 0.6 고정
         * baseSheet: 0.4 고정
         * overlaySheet: 0.4 -> 1.0
         */
        val calendarFraction by remember(sheetWeight) {
            derivedStateOf {
                if (sheetWeight <= 0.4f) 1f - sheetWeight else 0.6f
            }
        }

        val baseSheetFraction by remember(sheetWeight) {
            derivedStateOf {
                if (sheetWeight <= 0.4f) sheetWeight else 0.4f
            }
        }

        val showOverlay by remember(sheetWeight) {
            derivedStateOf { sheetWeight > 0.4f }
        }

        val calendarHeight by remember(fullHeight, calendarFraction) {
            derivedStateOf { fullHeight * calendarFraction }
        }

        val baseSheetHeight by remember(fullHeight, baseSheetFraction) {
            derivedStateOf { fullHeight * baseSheetFraction }
        }

        val overlaySheetHeight by remember(fullHeight, sheetWeight, showOverlay) {
            derivedStateOf { if (showOverlay) fullHeight * sheetWeight else 0.dp }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            /*
             * base layout
             * - 0.0 ~ 0.4 구간에서는 calendar 와 sheet 가 실제로 붙어서 같이 움직임
             * - 0.4 이상에서도 calendar 0.6 / sheet 자리 0.4 는 유지
             */
            Column(modifier = Modifier.fillMaxSize()) {
                val calendarHeaderHeight = 110.dp
                val calendarBodyHeight = (calendarHeight - calendarHeaderHeight).coerceAtLeast(0.dp)

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(calendarHeight)
                            .background(Gray900),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(calendarHeaderHeight),
                    ) {
                        CalendarTopBar(
                            currentMonth = uiState.currentMonth,
                            onClickPreviousMonth = interactionState::onClickPreviousMonth,
                            onClickNextMonth = interactionState::onClickNextMonth,
                            modifier =
                                Modifier
                                    .padding(horizontal = 22.dp)
                                    .padding(top = 22.dp),
                        )

                        Spacer(Modifier.height(20.dp))

                        CalendarWeekHeader(
                            modifier = Modifier.padding(horizontal = 22.dp),
                        )
                    }

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(calendarBodyHeight)
                                .padding(horizontal = 22.dp)
                                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                    ) {
                        AnimatedContent(
                            targetState = uiState.currentMonth,
                            transitionSpec = {
                                val isNext = interactionState.transitionDirection == MonthTransitionDirection.Next

                                ContentTransform(
                                    targetContentEnter =
                                        slideInHorizontally(
                                            initialOffsetX = { fullWidth ->
                                                if (isNext) fullWidth else -fullWidth
                                            },
                                        ) + fadeIn(),
                                    initialContentExit =
                                        slideOutHorizontally(
                                            targetOffsetX = { fullWidth ->
                                                if (isNext) -fullWidth else fullWidth
                                            },
                                        ) + fadeOut(),
                                    sizeTransform = SizeTransform(clip = false),
                                )
                            },
                            label = "calendar-month-transition",
                        ) { animatedMonth ->
                            val animatedCells =
                                remember(
                                    animatedMonth,
                                    uiState.selectedDate,
                                    uiState.schedulesByDate,
                                ) {
                                    uiState.copy(currentMonth = animatedMonth).toCalendarCells()
                                }

                            CalendarMonthGrid(
                                cells = animatedCells,
                                eventLabelAlpha = eventLabelAlpha,
                                eventDotAlpha = hiddenToPeek,
                                bodyHeight = calendarBodyHeight,
                                onSelectDate = interactionState::onDateClick,
                            )
                        }
                    }
                }

                if (baseSheetHeight > 0.dp) {
                    if (showOverlay) {
                        /*
                         * 0.4 이상에서는 base 영역은 자리만 유지
                         * 실제 내용은 overlay sheet 가 담당
                         */
                        Spacer(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(baseSheetHeight),
                        )
                    } else {
                        /*
                         * Hidden -> Peek 구간
                         * calendar 하단에 sheet 상단이 붙어 있는 상태
                         */
                        CalendarBottomSheet(
                            selectedDate = uiState.selectedDate,
                            schedules = uiState.selectedDateScheduleItems,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(baseSheetHeight),
                        )
                    }
                }
            }

            /*
             * Peek -> Expanded 구간
             * calendar 0.6 / baseSheet 자리 0.4는 유지된 채
             * overlay sheet 가 위를 덮으며 0.4 -> 1.0으로 확장
             */
            if (showOverlay) {
                if (dimAlpha > 0.02f) {
                    Box(
                        modifier =
                            Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = dimAlpha)),
                    )
                }

                CalendarBottomSheet(
                    selectedDate = uiState.selectedDate,
                    schedules = uiState.selectedDateScheduleItems,
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(overlaySheetHeight),
                )
            }
        }
    }
}

@Composable
private fun rememberCalendarInteractionState(
    uiState: CalendarUiState,
    sheetState: CalendarSheetState,
    onSelectDate: (LocalDate) -> Unit,
    onMoveToPreviousMonth: () -> Unit,
    onMoveToNextMonth: () -> Unit,
    onShowPeekSheet: () -> Unit,
): CalendarInteractionState =
    remember(
        sheetState,
        onSelectDate,
        onMoveToPreviousMonth,
        onMoveToNextMonth,
        onShowPeekSheet,
    ) {
        CalendarInteractionState(
            currentMonthProvider = { uiState.currentMonth },
            sheetState = sheetState,
            onSelectDate = onSelectDate,
            onMoveToPreviousMonth = onMoveToPreviousMonth,
            onMoveToNextMonth = onMoveToNextMonth,
            onShowPeekSheet = onShowPeekSheet,
        )
    }
