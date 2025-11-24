package com.dh.ondot.presentation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.pretendard_black
import ondot.core.design_system.generated.resources.pretendard_bold
import ondot.core.design_system.generated.resources.pretendard_extra_bold
import ondot.core.design_system.generated.resources.pretendard_extra_light
import ondot.core.design_system.generated.resources.pretendard_light
import ondot.core.design_system.generated.resources.pretendard_medium
import ondot.core.design_system.generated.resources.pretendard_regular
import ondot.core.design_system.generated.resources.pretendard_semi_bold
import ondot.core.design_system.generated.resources.pretendard_thin
import org.jetbrains.compose.resources.Font

@Composable
fun OnDotFontFamily() = FontFamily(
    Font(Res.font.pretendard_thin, FontWeight.Thin),
    Font(Res.font.pretendard_extra_light, FontWeight.ExtraLight),
    Font(Res.font.pretendard_light, FontWeight.Light),
    Font(Res.font.pretendard_regular, FontWeight.Normal),
    Font(Res.font.pretendard_medium, FontWeight.Medium),
    Font(Res.font.pretendard_semi_bold, FontWeight.SemiBold),
    Font(Res.font.pretendard_bold, FontWeight.Bold),
    Font(Res.font.pretendard_extra_bold, FontWeight.ExtraBold),
    Font(Res.font.pretendard_black, FontWeight.Black)
)

data class OnDotTypography(
    val titleLargeL: TextStyle,
    val titleLargeM: TextStyle,
    val titleLargeR: TextStyle,
    val titleMediumSB: TextStyle,
    val titleMediumM: TextStyle,
    val titleMediumL: TextStyle,
    val titleMediumR: TextStyle,
    val titleSmallM: TextStyle,
    val titleSmallR: TextStyle,
    val titleSmallSB: TextStyle,
    val bodyLargeSB: TextStyle,
    val bodyLargeR1: TextStyle,
    val bodyLargeR2: TextStyle,
    val bodyMediumM: TextStyle,
    val bodyMediumR: TextStyle,
    val bodyMediumSB: TextStyle,
    val bodySmallR1: TextStyle,
    val bodySmallR2: TextStyle,
    val bodySmallR3: TextStyle
)

@Composable
fun OnDotTypo(): OnDotTypography {
    val fontFamily = OnDotFontFamily()

    fun lh(size: Int) = (size * 1.4f).sp

    return OnDotTypography(
        titleLargeL = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Light, fontSize = 42.sp, lineHeight = lh(42)),
        titleLargeM = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 60.sp, lineHeight = lh(60)),
        titleLargeR = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 32.sp, lineHeight = lh(32)),

        titleMediumSB = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = lh(24)),
        titleMediumM = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 24.sp, lineHeight = lh(24)),
        titleMediumL = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Light, fontSize = 24.sp, lineHeight = lh(24)),
        titleMediumR = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 20.sp, lineHeight = lh(20)),

        titleSmallM = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = lh(18)),
        titleSmallR = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = lh(18)),
        titleSmallSB = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = lh(17)),

        bodyLargeSB = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = lh(16)),
        bodyLargeR1 = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = lh(16)),
        bodyLargeR2 = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = lh(15)),

        bodyMediumM = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = lh(14)),
        bodyMediumR = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = lh(14)),
        bodyMediumSB = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = lh(12)),

        bodySmallR1 = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = lh(12)),
        bodySmallR2 = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = lh(11)),
        bodySmallR3 = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 8.sp, lineHeight = lh(8)),
    )
}