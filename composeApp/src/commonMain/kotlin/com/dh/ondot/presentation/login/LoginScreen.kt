package com.dh.ondot.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.theme.IOS
import com.dh.ondot.presentation.ui.theme.KAKAO_LOGIN_BUTTON_TEXT
import com.dh.ondot.presentation.ui.theme.OnDotColor
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_login
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel { LoginViewModel() }
) {
    LoginContent(
        onKakaoLogin = { viewModel.onKakaoLogin() }
    )
}

@Composable
fun LoginContent(
    onKakaoLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OnDotColor.Gray900),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .background(OnDotColor.GradientLogin)
        )

        Image(
            painter = painterResource(Res.drawable.ic_login),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 189.dp)
                .width(240.dp)
                .height(85.dp)
        )

        LoginButtons(
            onKakaoLogin = onKakaoLogin
        )
    }
}

@Composable
fun LoginButtons(
    onKakaoLogin: () -> Unit = {},
    onAppleLogin: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.weight(1f))

        OnDotButton(
            buttonText = KAKAO_LOGIN_BUTTON_TEXT,
            buttonType = ButtonType.Kakao,
            onClick = onKakaoLogin
        )

        if (getPlatform().name == IOS) {
            // TODO: Apple Login Button
            Spacer(modifier = Modifier.height(21.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}