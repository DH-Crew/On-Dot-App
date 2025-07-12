package com.dh.ondot.presentation.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray200
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.SETTING_ALARM_DEFAULT
import com.dh.ondot.presentation.ui.theme.SETTING_CUSTOMER_SERVICE
import com.dh.ondot.presentation.ui.theme.SETTING_HOME_ADDRESS
import com.dh.ondot.presentation.ui.theme.SETTING_NAV_MAP
import com.dh.ondot.presentation.ui.theme.SETTING_PREPARE_TIME
import com.dh.ondot.presentation.ui.theme.SETTING_SERVICE_POLICY
import com.dh.ondot.presentation.ui.theme.WORD_ACCOUNT
import com.dh.ondot.presentation.ui.theme.WORD_GENERAL
import com.dh.ondot.presentation.ui.theme.WORD_HELP
import com.dh.ondot.presentation.ui.theme.WORD_LOGOUT
import com.dh.ondot.presentation.ui.theme.WORD_SETTING
import com.dh.ondot.presentation.ui.theme.WORD_WITHDRAW
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_arrow_right_gray400
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingScreen(

) {
    SettingContent()
}

@Composable
fun SettingContent(

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900)
            .padding(horizontal = 22.dp)
            .padding(top = 24.dp)
    ) {
        OnDotText(text = WORD_SETTING, style = OnDotTextStyle.TitleMediumSB, color = Gray0)

        Spacer(modifier = Modifier.height(28.dp))

        SettingSection(
            header = WORD_GENERAL,
            sections = listOf(
                Pair(SETTING_HOME_ADDRESS, {}),
                Pair(SETTING_NAV_MAP, {}),
                Pair(SETTING_ALARM_DEFAULT, {}),
                Pair(SETTING_PREPARE_TIME, {})
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingSection(
            header = WORD_HELP,
            sections = listOf(
                Pair(SETTING_CUSTOMER_SERVICE, {}),
                Pair(SETTING_SERVICE_POLICY, {})
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingSection(
            header = WORD_ACCOUNT,
            sections = listOf(
                Pair(WORD_WITHDRAW, {}),
                Pair(WORD_LOGOUT, null)
            )
        )
    }
}

@Composable
fun SettingSection(
    header: String,
    sections: List<Pair<String, (() -> Unit)?>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OnDotText(text = header, style = OnDotTextStyle.BodyMediumM, color = Gray200, modifier = Modifier.padding(start = 20.dp))

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(thickness = (0.5).dp, color = Gray600, modifier = Modifier.padding(horizontal = 4.dp))

        Spacer(modifier = Modifier.height(16.dp))

        sections.forEachIndexed { index, section ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                OnDotText(text = section.first, style = OnDotTextStyle.BodyLargeR1, color = Gray0)

                Spacer(modifier = Modifier.weight(1f))

                if (section.second != null) {
                    Image(
                        painter = painterResource(Res.drawable.ic_arrow_right_gray400),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { section.second?.invoke() }
                    )
                }
            }

            if (index != sections.lastIndex) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}