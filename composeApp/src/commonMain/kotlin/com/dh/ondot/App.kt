package com.dh.ondot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dh.ondot.presentation.login.LoginScreen
import com.dh.ondot.presentation.splash.SplashScreen
import com.dh.ondot.presentation.ui.theme.OnDotTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    OnDotTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LoginScreen()
        }
    }
}