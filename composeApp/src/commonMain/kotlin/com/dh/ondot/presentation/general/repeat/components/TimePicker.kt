package com.dh.ondot.presentation.general.repeat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.WORD_AM
import com.dh.ondot.presentation.ui.theme.WORD_PM
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime

@Composable
fun DialPicker(
    items: List<String>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    infinite: Boolean = true,
    onValueChange: (String) -> Unit = {}
) {
    val visibleItemCount = 5
    val itemHeight = 33.dp
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // 스크롤이 멈춘 시점에 콜백 호출
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .drop(1)
            .filter { inProgress -> !inProgress }
            .map {
                val centerIndex = listState.firstVisibleItemIndex + visibleItemCount / 2

                if (infinite) items[centerIndex % items.size]
                else {
                    val real = centerIndex - 2
                    items.getOrNull(real)?.takeIf { it.isNotEmpty() } ?: ""
                }
            }
            .filter { it.isNotEmpty() }
            .distinctUntilChanged()
            .collect { index ->
                onValueChange(index)
            }
    }

    if (infinite) {
        val repeatCount = 1000
        val itemCount = repeatCount * items.size

        // 무한 스크롤에서 가운데 위치로 자동 스크롤
        LaunchedEffect(listState) {
            val middle = itemCount / 2
            val startIndex = middle - (middle % items.size)

            listState.scrollToItem(startIndex - 2)
        }

        LazyColumn(
            state = listState,
            modifier = modifier
                .height(itemHeight * visibleItemCount),
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(count = itemCount) { index ->
                val text = items[index % items.size]
                val firstVisibleItemIndex = listState.firstVisibleItemIndex
                val color = when (index) {
                    firstVisibleItemIndex + 2 -> Gray0
                    else -> Gray400
                }

                Box(
                    modifier = Modifier
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    if (text.isNotEmpty()) {
                        OnDotText(
                            text = text,
                            style = OnDotTextStyle.TitleSmallR,
                            color = color
                        )
                    }
                }
            }
        }
    } else {
        val extended = listOf("", "") + items + listOf("", "")

        LazyColumn(
            state = listState,
            modifier = modifier.height(itemHeight * visibleItemCount),
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(extended.size) { index ->
                val text = extended[index]
                val firstVisible = listState.firstVisibleItemIndex
                val color = if (index == firstVisible + 2) Gray0 else Gray400

                Box(
                    modifier = Modifier.height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    if (text.isNotEmpty()) {
                        OnDotText(
                            text = text,
                            style = OnDotTextStyle.TitleSmallR,
                            color = color
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimePicker(
    periodState: LazyListState,
    hourState: LazyListState,
    minuteState: LazyListState,
    modifier: Modifier = Modifier,
    onTimeSelected: (LocalTime) -> Unit
) {
    val period = listOf(WORD_AM, WORD_PM)
    val hour = (1..12).toList().map { it.toString().padStart(2, '0') }
    val minute = (0..55 step 5).toList().map { it.toString().padStart(2, '0') }
    var selPeriod by remember { mutableStateOf(period.first()) }
    var selHour   by remember { mutableStateOf(hour.first()) }
    var selMin    by remember { mutableStateOf(minute.first()) }

    // 값이 하나라도 바뀌면 onTimeSelected 콜백 호출
    LaunchedEffect(selPeriod, selHour, selMin) {
        val h = selHour.toInt().let { hh ->
            when (selPeriod) {
                WORD_PM -> (hh % 12) + 12
                else -> hh % 12
            }
        }
        val m = selMin.toInt()
        onTimeSelected(LocalTime(h, m))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(33.dp)
                .background(Gray600, RoundedCornerShape(8.dp))
        )

        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            DialPicker(
                items = period,
                listState = periodState,
                infinite = false,
                onValueChange = { selPeriod = it }
            )

            Spacer(modifier = Modifier.width(46.dp))

            DialPicker(
                items = hour,
                listState = hourState,
                onValueChange = { selHour = it }
            )

            Spacer(modifier = Modifier.width(40.dp))

            DialPicker(
                items = minute,
                listState = minuteState,
                onValueChange = { selMin = it }
            )
        }
    }
}