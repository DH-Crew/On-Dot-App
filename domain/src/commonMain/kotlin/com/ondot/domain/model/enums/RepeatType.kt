package com.dh.ondot.domain.model.enums

import com.dh.ondot.presentation.ui.theme.WORD_EVERYDAY
import com.dh.ondot.presentation.ui.theme.WORD_WEEKDAY
import com.dh.ondot.presentation.ui.theme.WORD_WEEKEND

enum class RepeatType(val index: Int, val title: String) {
    EVERYDAY(0, WORD_EVERYDAY),
    WEEKDAY(1, WORD_WEEKDAY),
    WEEKEND(2, WORD_WEEKEND);
}