package com.dh.ondot.presentation.edit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate

@Composable
fun EditDateBottomSheet(
    isRepeat: Boolean,
    repeatDays: Set<Int>,
    currentDate: LocalDate,
    onEditDate: (Boolean, Set<Int>, LocalDate) -> Unit,
) {
    var isRepeatState by remember(isRepeat) { mutableStateOf(isRepeat) }
    var repeatDaysState by remember(repeatDays) { mutableStateOf(repeatDays) }
    var currentDateState by remember(currentDate) { mutableStateOf(currentDate) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        
    }
}