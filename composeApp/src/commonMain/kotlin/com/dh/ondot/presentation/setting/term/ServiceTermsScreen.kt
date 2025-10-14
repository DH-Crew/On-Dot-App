package com.dh.ondot.presentation.setting.term

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.core.platform.WebView
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.SERVICE_TERMS_TITLE
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.TopBarType

@Composable
fun ServiceTermsScreen(
    popScreen: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Gray900)
                .padding(horizontal = 22.dp)
                .padding(bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            TopBar(
                type = TopBarType.BACK,
                onClick = popScreen
            )

            OnDotText(
                text = SERVICE_TERMS_TITLE,
                style = OnDotTextStyle.TitleSmallM,
                color = Gray0,
                modifier = Modifier.padding(top = if (getPlatform().name == ANDROID) 50.dp else 70.dp)
            )
        }

        WebView(
            url = "https://ondotdh.notion.site/Ondot-1e1d775a8a04802495c7cc44cac766cc",
            modifier = Modifier.fillMaxSize()
        )
    }
}