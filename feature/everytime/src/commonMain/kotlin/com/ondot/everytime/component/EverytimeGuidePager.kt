package com.ondot.everytime.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ondot.designsystem.theme.OnDotColor.Gray500
import com.ondot.designsystem.theme.OnDotColor.Green500
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_landing_step1
import ondot.core.design_system.generated.resources.ic_landing_step2
import ondot.core.design_system.generated.resources.ic_landing_step3
import org.jetbrains.compose.resources.DrawableResource

data class EverytimeGuideStep(
    val number: Int,
    val title: String,
    val description: String,
    val image: DrawableResource,
)

internal val everytimeGuideSteps =
    listOf(
        EverytimeGuideStep(
            number = 1,
            title = "에브리타임에서 시간표를 전체 공개로 변경",
            description = "설정 아이콘 → 공개 범위 변경 → 전체 공개",
            image = Res.drawable.ic_landing_step1,
        ),
        EverytimeGuideStep(
            number = 2,
            title = "공유 아이콘을 눌러 링크를 복사",
            description = "시간표 화면 우측 상단의 공유 버튼 선택",
            image = Res.drawable.ic_landing_step2,
        ),
        EverytimeGuideStep(
            number = 3,
            title = "복사된 URL을 입력창에 붙여넣기",
            description = "터치하면 복사된 링크가 자동으로 입력돼요",
            image = Res.drawable.ic_landing_step3,
        ),
    )

@Composable
fun EverytimeGuidePager(
    modifier: Modifier = Modifier,
) {
    val steps = remember { everytimeGuideSteps }
    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = { steps.size },
        )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                pageSize = PageSize.Fill,
                contentPadding = PaddingValues(horizontal = 32.dp),
                pageSpacing = 12.dp,
            ) { page ->
                val step = steps[page]

                StepItem(
                    number = step.number,
                    title = step.title,
                    description = step.description,
                    image = step.image,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        PagerIndicator(
            pageCount = steps.size,
            currentPage = pagerState.currentPage,
        )
    }
}

//@Composable
//fun EverytimeGuidePager(
//    modifier: Modifier = Modifier,
//) {
//    val steps = remember { everytimeGuideSteps }
//    val pagerState =
//        rememberPagerState(
//            initialPage = 0,
//            pageCount = { steps.size },
//        )
//
//    Column(
//        modifier = modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        HorizontalPager(
//            state = pagerState,
//            modifier =
//                Modifier
//                    .fillMaxWidth()
//                    .weight(1f),
//        ) { page ->
//            val step = steps[page]
//
//            StepItem(
//                number = step.number,
//                title = step.title,
//                description = step.description,
//                image = step.image,
//            )
//        }
//
//        Spacer(Modifier.height(20.dp))
//
//        PagerIndicator(
//            pageCount = steps.size,
//            currentPage = pagerState.currentPage,
//        )
//    }
//}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier =
                    Modifier
                        .size(8.dp)
                        .background(
                            color = if (index == currentPage) Green500 else Gray500,
                            shape = CircleShape,
                        ),
            )

            if (index != pageCount - 1) {
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}
