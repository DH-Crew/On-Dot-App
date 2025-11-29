package com.ondot.design_system.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.dh.ondot.presentation.ui.theme.LocalOnDotTypography
import com.ondot.domain.model.enums.OnDotTextStyle

@Composable
fun OnDotTextStyle.toTextStyle(): TextStyle {
    val typo = LocalOnDotTypography.current

    return when (this) {
        OnDotTextStyle.TitleLargeL -> typo.titleLargeL
        OnDotTextStyle.TitleLargeM -> typo.titleLargeM
        OnDotTextStyle.TitleLargeR -> typo.titleLargeR
        OnDotTextStyle.TitleMediumSB -> typo.titleMediumSB
        OnDotTextStyle.TitleMediumM -> typo.titleMediumM
        OnDotTextStyle.TitleMediumL -> typo.titleMediumL
        OnDotTextStyle.TitleMediumR -> typo.titleMediumR
        OnDotTextStyle.TitleSmallM -> typo.titleSmallM
        OnDotTextStyle.TitleSmallR -> typo.titleSmallR
        OnDotTextStyle.TitleSmallSB -> typo.titleSmallSB
        OnDotTextStyle.BodyLargeSB -> typo.bodyLargeSB
        OnDotTextStyle.BodyLargeR1 -> typo.bodyLargeR1
        OnDotTextStyle.BodyLargeR2 -> typo.bodyLargeR2
        OnDotTextStyle.BodyMediumM -> typo.bodyMediumM
        OnDotTextStyle.BodyMediumR -> typo.bodyMediumR
        OnDotTextStyle.BodyMediumSB -> typo.bodyMediumSB
        OnDotTextStyle.BodySmallR1 -> typo.bodySmallR1
        OnDotTextStyle.BodySmallR2 -> typo.bodySmallR2
        OnDotTextStyle.BodySmallR3 -> typo.bodySmallR3
    }
}
