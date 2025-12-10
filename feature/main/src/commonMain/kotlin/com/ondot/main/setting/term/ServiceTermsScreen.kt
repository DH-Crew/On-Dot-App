package com.ondot.main.setting.term

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.SERVICE_NOTIFICATION_TITLE
import com.dh.ondot.presentation.ui.theme.SERVICE_TERMS_TITLE
import com.ondot.design_system.components.OnDotText
import com.ondot.design_system.components.TopBar
import com.ondot.design_system.getPlatform
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.TopBarType
import com.ondot.platform.webview.WebView
import com.ondot.util.AnalyticsLogger

@Composable
fun ServiceTermsScreen(
    isNotification: Boolean,
    popScreen: () -> Unit
) {
    LaunchedEffect(Unit) {
        AnalyticsLogger.logEvent("screen_view_service_term")
    }

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
                text = if (isNotification) SERVICE_NOTIFICATION_TITLE else SERVICE_TERMS_TITLE,
                style = OnDotTextStyle.TitleSmallM,
                color = Gray0,
                modifier = Modifier.padding(top = if (getPlatform() == ANDROID) 50.dp else 70.dp)
            )
        }

        WebView(
            url = if (isNotification) "https://www.notion.so/readyberry/251211-2c5d775a8a0480158f14c401c7823478?source=copy_link" else "https://ondotdh.notion.site/Ondot-1e1d775a8a04802495c7cc44cac766cc",
            modifier = Modifier.fillMaxSize()
        )
    }
}