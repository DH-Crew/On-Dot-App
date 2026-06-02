package com.ondot.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.layout

/**
 * Compose의 Layout 단계에서
 * measure(크기 계산), layout(위치 계산) 각각의 시간이 얼마나 걸리는지를 System Trace에 기록하는 함수
 * */
fun Modifier.traceLayout(name: String): Modifier =
    layout { measurable, constraints ->
        val placeable =
            systemTrace("measure:$name") {
                measurable.measure(constraints)
            }

        systemTrace("layout:$name") {
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }
    }

/**
 * Compose의 Draw 단계에서
 * draw(그리기) 시간이 얼마나 걸리는지를 System Trace에 기록하는 함수
 * */
fun Modifier.traceDraw(name: String): Modifier =
    drawWithContent {
        systemTrace("draw:$name") {
            drawContent()
        }
    }
