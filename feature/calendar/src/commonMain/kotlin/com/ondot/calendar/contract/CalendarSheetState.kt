package com.ondot.calendar.contract

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private val SheetSpringSpec =
    spring<Float>(
        dampingRatio = 0.9f,
        stiffness = Spring.StiffnessMediumLow,
    )

enum class CalendarSheetAnchor {
    Hidden,
    Peek,
    Expanded,
}

@Stable
class CalendarSheetState internal constructor(
    initialAnchor: CalendarSheetAnchor,
) {
    var currentAnchor: CalendarSheetAnchor = initialAnchor
        private set

    var currentWeight by mutableFloatStateOf(initialAnchor.toWeight())
        private set

    /**
     * Hidden(0f) -> Peek(0.4f)
     */
    val hiddenToPeekProgress: Float
        get() = (currentWeight / PEEK_WEIGHT).coerceIn(0f, 1f)

    /**
     * Peek(0.4f) -> Expanded(1f)
     */
    val peekToExpandedProgress: Float
        get() =
            ((currentWeight - PEEK_WEIGHT) / (EXPANDED_WEIGHT - PEEK_WEIGHT))
                .coerceIn(0f, 1f)

    fun snapBy(deltaWeight: Float) {
        currentWeight = (currentWeight + deltaWeight).coerceIn(HIDDEN_WEIGHT, EXPANDED_WEIGHT)
        syncAnchorWithWeight()
    }

    suspend fun animateTo(anchor: CalendarSheetAnchor) {
        val animatable = Animatable(currentWeight)
        animatable.animateTo(
            targetValue = anchor.toWeight(),
            animationSpec = SheetSpringSpec,
        ) {
            currentWeight = value
        }
        currentAnchor = anchor
        currentWeight = anchor.toWeight()
    }

    fun resolveTarget(): CalendarSheetAnchor {
        val current = currentWeight
        return when {
            current < HIDDEN_TO_PEEK_THRESHOLD -> CalendarSheetAnchor.Hidden
            current < PEEK_TO_EXPANDED_THRESHOLD -> CalendarSheetAnchor.Peek
            else -> CalendarSheetAnchor.Expanded
        }
    }

    fun syncAnchorWithWeight() {
        currentAnchor =
            when {
                currentWeight <= HIDDEN_WEIGHT -> CalendarSheetAnchor.Hidden
                currentWeight <= PEEK_WEIGHT -> CalendarSheetAnchor.Peek
                else -> CalendarSheetAnchor.Expanded
            }
    }

    private fun CalendarSheetAnchor.toWeight(): Float =
        when (this) {
            CalendarSheetAnchor.Hidden -> HIDDEN_WEIGHT
            CalendarSheetAnchor.Peek -> PEEK_WEIGHT
            CalendarSheetAnchor.Expanded -> EXPANDED_WEIGHT
        }

    companion object {
        const val HIDDEN_WEIGHT = 0f
        const val PEEK_WEIGHT = 0.4f
        const val EXPANDED_WEIGHT = 1f

        const val HIDDEN_TO_PEEK_THRESHOLD = 0.22f
        const val PEEK_TO_EXPANDED_THRESHOLD = 0.7f
    }
}

@Composable
fun rememberCalendarSheetState(initialAnchor: CalendarSheetAnchor = CalendarSheetAnchor.Hidden): CalendarSheetState =
    remember { CalendarSheetState(initialAnchor) }
