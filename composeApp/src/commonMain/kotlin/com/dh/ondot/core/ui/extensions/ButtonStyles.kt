package com.dh.ondot.core.ui.extensions

import androidx.compose.runtime.Composable
import com.dh.ondot.core.ui.model.ButtonStyle
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.presentation.ui.theme.OnDotColor
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_apple
import ondot.composeapp.generated.resources.ic_kakao

@Composable
fun ButtonType.styles(): ButtonStyle {
    return when(this) {
        ButtonType.Kakao -> ButtonStyle(backgroundColor = OnDotColor.Kakao, fontColor = OnDotColor.Gray900, drawableResource = Res.drawable.ic_kakao)
        ButtonType.Apple -> ButtonStyle(backgroundColor = OnDotColor.Gray900, fontColor = OnDotColor.Gray0, drawableResource = Res.drawable.ic_apple)
        ButtonType.Green500 -> ButtonStyle(backgroundColor = OnDotColor.Green500, fontColor = OnDotColor.Gray900)
        ButtonType.Green600 -> ButtonStyle(backgroundColor = OnDotColor.Green600, fontColor = OnDotColor.Gray900)
        ButtonType.Gray300 -> ButtonStyle(backgroundColor = OnDotColor.Gray300, fontColor = OnDotColor.Gray900)
        ButtonType.Gray400 -> ButtonStyle(backgroundColor = OnDotColor.Gray400, fontColor = OnDotColor.Gray0)
        ButtonType.Gradient -> ButtonStyle(backgroundColor = OnDotColor.Gray700, fontColor = OnDotColor.Gray0)
        ButtonType.Red -> ButtonStyle(backgroundColor = OnDotColor.Red, fontColor = OnDotColor.Gray0)
    }
}