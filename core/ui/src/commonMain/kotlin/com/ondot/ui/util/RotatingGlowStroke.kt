package com.ondot.ui.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

//@Composable
//fun Modifier.rotatingGlowStroke(
//    enabled: Boolean,
//    cornerRadius: Dp = 12.dp,
//    strokeWidth: Dp = 1.0.dp,
//
//    // 하이라이트(움직이는 구간)의 선두/꼬리 색상.
//    // startColor → endColor로 그라데이션이 흐른다.
//    highlightStartColor: Color,
//    highlightEndColor: Color,
//
//    // 하이라이트가 테두리 전체 둘레 중 차지하는 비율(0~1).
//    // 예: 0.65이면 둘레의 65% 길이가 하이라이트로 칠해진다.
//    highlightLengthFraction: Float = 0.5f,
//
//    // 하이라이트가 한 바퀴 도는 주기(ms)
//    periodMs: Int = 5000,
//
//    // StrokeCap:
//    // · Butt: 끝이 딱 잘린 형태(두 조각으로 나뉘는 wrap-around 구간에서 경계가 덜 티나게 설정)
//    cap: StrokeCap = StrokeCap.Butt
//): Modifier {
//    if (!enabled) return this
//
//    /**
//     * rememberInfiniteTransition은 Compose 애니메이션의 무한 반복을 구현할 때 사용하는 Transition
//     * infinite transition 생성을 통해 무한 애니메이션을 제어할 수 있음
//     * infinite transition의 animateFloat 메서드는 initialValue ~ targetValue 사이의 값을 반복하며 무한 애니메이션을 재생
//     * animationSpec에 전달되는 반복 규칙에 따라 애니메이션이 재생됨
//     * LinearEasing 옵션은 애니메이션의 속도를 선형적으로 조절하는 것
//     * */
//    val transition = rememberInfiniteTransition(label = "rotatingGlowStroke")
//    val progress by transition.animateFloat(
//        initialValue = 0f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = periodMs, easing = LinearEasing)
//        ),
//        label = "progress"
//    )
//
//    return this.then(
//
//        /**
//         * drawWithCache를 사용하면 strokePx, radiusPx borderPath 등을 매 프레임마다 계산하지 않고 캐싱 적용
//         * */
//        Modifier.drawWithCache {
//            val strokePx = strokeWidth.toPx()
//            val radiusPx = cornerRadius.toPx()
//
//            /**
//             * Stroke는 경로의 중심선을 기준으로 양옆으로 두께가 생김
//             * strokeWidth가 10px이면, 경로 밖으로 5px + 안으로 5px 퍼짐
//             * inset 설정 없이 stroke를 그리면 절반이 바깥으로 나가서 잘려 보일 수 있음
//             * 따라서 stroke / 2 만큼 경로를 안쪽으로 이동시켜 잘리는 현상을 방지함
//             * */
//            val inset = strokePx / 2f
//
//            /**
//             * Path란?
//             * · 어떤 도형의 윤곽(경로)을 표현하는 벡터 형태의 자료구조
//             *   · line, curve, rect, roundRect 등을 하나의 경로로 구성할 수 있음
//             * · drawPath로 실제 화면에 경로를 그릴 수 있음
//             * · roundRect로 모서리가 둥근 테두리를 Path로 만들고 있음
//             *
//             * left, right, top, bottom으로 사각형의 경계를 정의함
//             * addRoundRect로 이 Path에 라운드 사각형 윤곽을 추가함
//             * 즉, 테두리 전체를 나타내는 경로가 됨
//             * */
//            val borderPath = Path().apply {
//                addRoundRect(
//                    RoundRect(
//                        left = inset,
//                        top = inset,
//                        right = size.width - inset,
//                        bottom = size.height - inset,
//                        cornerRadius = CornerRadius(radiusPx, radiusPx)
//                    )
//                )
//            }
//
//            /**
//             * PathMeasure란?
//             * · Path를 길이 기반으로 다룰 수 있게 해주는 도구
//             * · 제공하는 기능
//             *   · length: 경로의 전체 길이(둘레/곡선 길이)를 계산
//             *   · getSegment(start, end, outPath, startWithMoveTo): 경로의 특정 구간만 잘라서 outPath 추출
//             *
//             * 즉, 테두리의 길이를 측정한 다음, 그 길이의 일부분만 잘라서 그리는 것이 가능함
//             * */
//            val borderMeasure = PathMeasure().apply {
//                /**
//                 * setPath(path, forceClosed):
//                 * · forceClosed = false: path가 닫혀있지 않아도 있는 그대로 측정함
//                 * · 라운드 사각형은 사실상 폐곡선처럼 동작하지만 여기서는 false로 설정
//                 * */
//                setPath(borderPath, false)
//            }
//            /**
//             * 테두리 전체 길이(둘레). 0 방지를 위해 최소 1f로 보정
//             * */
//            val borderLength = borderMeasure.length.coerceAtLeast(1f)
//            /**
//             * 하이라이트 구간의 길이
//             * · 전체 둘레(borderLength) * 비율(highlightLengthFraction)
//             * · 최소 1f, 최대 borderLength로 고정
//             * */
//            val segLen = (borderLength * highlightLengthFraction).coerceIn(1f, borderLength)
//
//            /**
//             * segment Path에 진행 방향으로 그라데이션을 적용해 그리는 헬퍼 메서드
//             * */
//            fun DrawScope.drawSegmentWithGradient(
//                segPath: Path,
//                c0: Color,
//                c1: Color
//            ) {
//                /**
//                 * segPath도 Path이므로 PathMeasure로 길이를 측정할 수 있음
//                 * */
//                val pm = PathMeasure().apply { setPath(segPath, false) }
//                val segLength = pm.length
//                if (segLength <= 0f) return
//
//                /**
//                 * segPath는 전체 Path에서 일부를 추출해서 파라미터로 넘긴 값
//                 * 그 일부분에서 시작점과 끝점을 구함
//                 * getPosition은 Path에서 distance 만큼 이동한 좌표를 반환함
//                 * */
//                val p0: Offset = pm.getPosition(0f)
//                val p1: Offset = pm.getPosition(segLength)
//
//                /**
//                 * start → end 방향으로 선형 그라데이션을 생성
//                 * 여기서 start = p0, end = p1으로 설정하면 segment 진행 방향으로 색이 흐르게 됨
//                 * */
//                val brush = Brush.linearGradient(
//                    colors = listOf(c0, c1),
//                    start = p0,
//                    end = p1
//                )
//
//                drawPath(
//                    path = segPath,
//                    brush = brush,
//                    style = Stroke(width = strokePx, cap = cap)
//                )
//            }
//
//            onDrawWithContent {
//
//                /**
//                 * 원래 콘텐츠를 먼저 그림
//                 * Modifier가 적용된 컴포넌트 자체가 먼저 그려짐
//                 * */
//                drawContent()
//
//                /**
//                 * 이동하는 하이라이트 위치 계산
//                 * progress가 시간에 따라 0f → 1f로 값이 증가하기 때문에 borderLength를 곱해서 실제 거리로 변환
//                 * borderLength로 나누는 이유는 1바퀴 돌고 다시 0으로 이어지게 만드는 wrap 효과를 위함
//                 * */
//                val start = (progress * borderLength) % borderLength
//                val end = start + segLen
//
//                /**
//                 * borderMeasure.getSegment(startDistance, endDistance, outPath, startWithMoveTo):
//                 * · borderPath 전체 중 startDistance..endDistance 부분만 잘라 outPath에 저장
//                 *
//                 * RoundRect는 폐곡선형이기 때문에 두가지 케이스가 존재
//                 * 1. end가 borderLength를 넘지 않을 때
//                 * 2. end가 borderLength를 넘을 때
//                 *
//                 * 1의 경우 하나의 조각으로 끝나기 때문에 drawSegmentWithGradient에 그대로 전달해서 그리면 됨
//                 * 2의 경우 end가 borderLength보다 값이 커져서 하나의 조각으로 그릴 수 없음
//                 *
//                 * 한 번에 그리지 못하는 이유
//                 * · PathMeasure.getSegment는 start, end 값이 전체 경로 범위 내(0..borderLength)에서
//                 *   설정되어야 잘라낼 수 있음
//                 * · 하지만 하이라이트가 한 바퀴 돌아서 0부터 다시 이어지는 경우는 end 값이 borderLength 값보다 커짐
//                 *   · 예를 들어서 전체 borderLength: 1000, start: 900, segLen: 400인 경우 end가 1300이 됨
//                 *   · 이때는 900 ~ 1000, 0 ~ 300 두 구간으로 잘라서 이어지게 그려야 함
//                 * */
//                if (end <= borderLength) {
//                    // 하나의 조각으로 끝나는 케이스
//                    val segPath = Path()
//                    borderMeasure.getSegment(start, end, segPath, true)
//
//                    drawSegmentWithGradient(
//                        segPath = segPath,
//                        c0 = highlightStartColor,
//                        c1 = highlightEndColor
//                    )
//                } else {
//                    // 두 조각으로 나눠 그리기
//                    val segPath1 = Path()
//                    val segPath2 = Path()
//
//                    val len1 = borderLength - start       // 1조각 길이
//                    val len2 = end - borderLength         // 2조각 길이
//
//                    borderMeasure.getSegment(start, borderLength, segPath1, true)
//                    borderMeasure.getSegment(0f, len2, segPath2, true)
//
//                    /**
//                     * 색 끊김 방지
//                     * 전체 하이라이트(c0 -> c1)에서 1조각이 차지하는 비율만큼 진행된 중간색을 만들어 이어줌
//                     * */
//                    val frac1 = (len1 / segLen).coerceIn(0f, 1f)
//                    val midColor = lerp(highlightStartColor, highlightEndColor, frac1)
//
//                    // 1조각: startColor -> mid
//                    drawSegmentWithGradient(segPath1, highlightStartColor, midColor)
//                    // 2조각: mid -> endColor
//                    drawSegmentWithGradient(segPath2, midColor, highlightEndColor)
//                }
//            }
//        }
//    )
//}

@Composable
fun Modifier.rotatingGlowStroke(
    enabled: Boolean,
    cornerRadius: Dp = 12.dp,
    strokeWidth: Dp = 1.0.dp,

    // 하이라이트(움직이는 구간)의 선두/꼬리 색상.
    // startColor → endColor로 그라데이션이 흐른다.
    highlightStartColor: Color,
    highlightEndColor: Color,

    // 하이라이트가 테두리 전체 둘레 중 차지하는 비율(0~1).
    // 예: 0.65이면 둘레의 65% 길이가 하이라이트로 칠해진다.
    highlightLengthFraction: Float = 0.5f,

    // 하이라이트가 한 바퀴 도는 주기(ms)
    periodMs: Int = 5000,

    // StrokeCap:
    // · Butt: 끝이 딱 잘린 형태(두 조각으로 나뉘는 wrap-around 구간에서 경계가 덜 티나게 설정)
    cap: StrokeCap = StrokeCap.Butt
): Modifier {
    if (!enabled) return this

    /**
     * rememberInfiniteTransition은 Compose 애니메이션의 무한 반복을 구현할 때 사용하는 Transition
     * infinite transition 생성을 통해 무한 애니메이션을 제어할 수 있음
     * infinite transition의 animateFloat 메서드는 initialValue ~ targetValue 사이의 값을 반복하며 무한 애니메이션을 재생
     * animationSpec에 전달되는 반복 규칙에 따라 애니메이션이 재생됨
     * LinearEasing 옵션은 애니메이션의 속도를 선형적으로 조절하는 것
     * */
    val transition = rememberInfiniteTransition(label = "rotatingGlowStroke")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = periodMs, easing = LinearEasing)
        ),
        label = "progress"
    )

    return this.then(

        /**
         * drawWithCache를 사용하면 strokePx, radiusPx borderPath 등을 매 프레임마다 계산하지 않고 캐싱 적용
         * */
        Modifier.drawWithCache {
            val strokePx = strokeWidth.toPx()
            val radiusPx = cornerRadius.toPx()

            /**
             * Stroke는 경로의 중심선을 기준으로 양옆으로 두께가 생김
             * strokeWidth가 10px이면, 경로 밖으로 5px + 안으로 5px 퍼짐
             * inset 설정 없이 stroke를 그리면 절반이 바깥으로 나가서 잘려 보일 수 있음
             * 따라서 stroke / 2 만큼 경로를 안쪽으로 이동시켜 잘리는 현상을 방지함
             * */
            val inset = strokePx / 2f

            /**
             * Path란?
             * · 어떤 도형의 윤곽(경로)을 표현하는 벡터 형태의 자료구조
             *   · line, curve, rect, roundRect 등을 하나의 경로로 구성할 수 있음
             * · drawPath로 실제 화면에 경로를 그릴 수 있음
             * · roundRect로 모서리가 둥근 테두리를 Path로 만들고 있음
             *
             * left, right, top, bottom으로 사각형의 경계를 정의함
             * addRoundRect로 이 Path에 라운드 사각형 윤곽을 추가함
             * 즉, 테두리 전체를 나타내는 경로가 됨
             * */
            val borderPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = inset,
                        top = inset,
                        right = size.width - inset,
                        bottom = size.height - inset,
                        cornerRadius = CornerRadius(radiusPx, radiusPx)
                    )
                )
            }

            /**
             * PathMeasure란?
             * · Path를 길이 기반으로 다룰 수 있게 해주는 도구
             * · 제공하는 기능
             *   · length: 경로의 전체 길이(둘레/곡선 길이)를 계산
             *   · getSegment(start, end, outPath, startWithMoveTo): 경로의 특정 구간만 잘라서 outPath 추출
             *
             * 즉, 테두리의 길이를 측정한 다음, 그 길이의 일부분만 잘라서 그리는 것이 가능함
             * */
            val borderMeasure = PathMeasure().apply {
                /**
                 * setPath(path, forceClosed):
                 * · forceClosed = false: path가 닫혀있지 않아도 있는 그대로 측정함
                 * · 라운드 사각형은 사실상 폐곡선처럼 동작하지만 여기서는 false로 설정
                 * */
                setPath(borderPath, false)
            }
            /**
             * 테두리 전체 길이(둘레). 0 방지를 위해 최소 1f로 보정
             * */
            val borderLength = borderMeasure.length.coerceAtLeast(1f)
            /**
             * 하이라이트 구간의 길이
             * · 전체 둘레(borderLength) * 비율(highlightLengthFraction)
             * · 최소 1f, 최대 borderLength로 고정
             * */
            val segLen = (borderLength * highlightLengthFraction).coerceIn(1f, borderLength)

            val segPath = Path()
            val segPath1 = Path()
            val segPath2 = Path()

            val eps = 0.001f
            fun safePosDistance(d: Float): Float =
                d.coerceIn(0f, (borderLength - eps).coerceAtLeast(eps))

            /**
             * segment Path에 진행 방향으로 그라데이션을 적용해 그리는 헬퍼 메서드
             * */
            fun DrawScope.drawSegmentWithGradient(
                startD: Float,
                endD: Float,
                out: Path,
                c0: Color,
                c1: Color
            ) {
                out.reset()

                val ok = borderMeasure.getSegment(startD, endD, out, true)
                if (!ok) return

                /**
                 * segPath는 전체 Path에서 일부를 추출해서 파라미터로 넘긴 값
                 * 그 일부분에서 시작점과 끝점을 구함
                 * getPosition은 Path에서 distance 만큼 이동한 좌표를 반환함
                 * */
                val p0 = borderMeasure.getPosition(safePosDistance(startD))
                val p1 = borderMeasure.getPosition(safePosDistance(endD))

                /**
                 * start → end 방향으로 선형 그라데이션을 생성
                 * 여기서 start = p0, end = p1으로 설정하면 segment 진행 방향으로 색이 흐르게 됨
                 * */
                val brush = Brush.linearGradient(
                    colors = listOf(c0, c1),
                    start = p0,
                    end = p1
                )

                drawPath(
                    path = out,
                    brush = brush,
                    style = Stroke(width = strokePx, cap = cap)
                )
            }

            onDrawWithContent {

                /**
                 * 원래 콘텐츠를 먼저 그림
                 * Modifier가 적용된 컴포넌트 자체가 먼저 그려짐
                 * */
                drawContent()

                /**
                 * 이동하는 하이라이트 위치 계산
                 * progress가 시간에 따라 0f → 1f로 값이 증가하기 때문에 borderLength를 곱해서 실제 거리로 변환
                 * borderLength로 나누는 이유는 1바퀴 돌고 다시 0으로 이어지게 만드는 wrap 효과를 위함
                 * */
                val start = (progress * borderLength) % borderLength
                val end = start + segLen

                /**
                 * borderMeasure.getSegment(startDistance, endDistance, outPath, startWithMoveTo):
                 * · borderPath 전체 중 startDistance..endDistance 부분만 잘라 outPath에 저장
                 *
                 * RoundRect는 폐곡선형이기 때문에 두가지 케이스가 존재
                 * 1. end가 borderLength를 넘지 않을 때
                 * 2. end가 borderLength를 넘을 때
                 *
                 * 1의 경우 하나의 조각으로 끝나기 때문에 drawSegmentWithGradient에 그대로 전달해서 그리면 됨
                 * 2의 경우 end가 borderLength보다 값이 커져서 하나의 조각으로 그릴 수 없음
                 *
                 * 한 번에 그리지 못하는 이유
                 * · PathMeasure.getSegment는 start, end 값이 전체 경로 범위 내(0..borderLength)에서
                 *   설정되어야 잘라낼 수 있음
                 * · 하지만 하이라이트가 한 바퀴 돌아서 0부터 다시 이어지는 경우는 end 값이 borderLength 값보다 커짐
                 *   · 예를 들어서 전체 borderLength: 1000, start: 900, segLen: 400인 경우 end가 1300이 됨
                 *   · 이때는 900 ~ 1000, 0 ~ 300 두 구간으로 잘라서 이어지게 그려야 함
                 * */
                if (end <= borderLength) {
                    // 하나의 조각으로 끝나는 케이스
                    drawSegmentWithGradient(
                        startD = start,
                        endD = end,
                        out = segPath,
                        c0 = highlightStartColor,
                        c1 = highlightEndColor
                    )
                } else {
                    // 두 조각으로 나눠 그리기

                    val len1 = borderLength - start       // 1조각 길이
                    val len2 = end - borderLength         // 2조각 길이

                    /**
                     * 색 끊김 방지
                     * 전체 하이라이트(c0 -> c1)에서 1조각이 차지하는 비율만큼 진행된 중간색을 만들어 이어줌
                     * */
                    val frac1 = (len1 / segLen).coerceIn(0f, 1f)
                    val midColor = lerp(highlightStartColor, highlightEndColor, frac1)

                    // 1조각: startColor -> mid
                    drawSegmentWithGradient(
                        startD = start,
                        endD = borderLength,
                        out = segPath1,
                        c0 = highlightStartColor,
                        c1 = midColor
                    )
                    // 2조각: mid -> endColor
                    drawSegmentWithGradient(
                        startD = 0f,
                        endD = len2,
                        out = segPath2,
                        c0 = midColor,
                        c1 = highlightEndColor
                    )
                }
            }
        }
    )
}