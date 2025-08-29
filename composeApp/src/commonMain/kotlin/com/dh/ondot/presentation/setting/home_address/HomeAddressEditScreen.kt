package com.dh.ondot.presentation.setting.home_address

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.enums.TopBarType
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.setting.SettingEvent
import com.dh.ondot.presentation.setting.SettingViewModel
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.PlaceSearchResultItem
import com.dh.ondot.presentation.ui.components.RoundedTextField
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray800
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.SETTING_HOME_ADDRESS_EDIT_TITLE
import com.dh.ondot.presentation.ui.theme.WORD_SAVE
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeAddressEditScreen(
    popScreen: () -> Unit,
    viewModel: SettingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SettingEvent.PopScreen -> popScreen()
            }
        }
    }

    HomeAddressEditContent(
        query = query,
        addressList = uiState.addressList,
        buttonEnabled = uiState.selectedHomeAddress.roadAddress.isNotBlank(),
        popScreen = popScreen,
        clearFocus = { focusManager.clearFocus() },
        onValueChange = {
            query = it
            viewModel.queryHomeAddress(it)
        },
        onClickAddress = {
            query = it.title
            viewModel.setSelectedAddress(it)
            focusManager.clearFocus()
        },
        onClickSave = viewModel::updateHomeAddress
    )
}

@Composable
fun HomeAddressEditContent(
    query: String,
    addressList: List<AddressInfo>,
    buttonEnabled: Boolean,
    popScreen: () -> Unit,
    clearFocus: () -> Unit,
    onValueChange: (String) -> Unit,
    onClickAddress: (AddressInfo) -> Unit,
    onClickSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900),
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {
            TopBar(
                type = TopBarType.BACK,
                onClick = popScreen
            )

            Spacer(modifier = Modifier.height(32.dp))

            OnDotText(text = SETTING_HOME_ADDRESS_EDIT_TITLE, style = OnDotTextStyle.TitleMediumM, color = Gray0)

            Spacer(modifier = Modifier.height(40.dp))

            HomeAddressSearchTextField(
                query = query,
                clearFocus = clearFocus,
                onValueChange = onValueChange
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        HorizontalDivider(thickness = 8.dp, modifier = Modifier.fillMaxWidth(), color = Gray800)

        AddressList(
            query = query,
            addressList = addressList,
            onClickAddress = onClickAddress
        )

        Spacer(modifier = Modifier.weight(1f))

        OnDotButton(
            buttonText = WORD_SAVE,
            buttonType = if (buttonEnabled) ButtonType.Green500 else ButtonType.Gray300,
            modifier = Modifier.padding(horizontal = 22.dp),
            onClick = {
                if (buttonEnabled) onClickSave()
            }
        )
        
        Spacer(modifier = Modifier.height(if (getPlatform().name == ANDROID) 16.dp else 37.dp))
    }
}

@Composable
private fun HomeAddressSearchTextField(
    query: String,
    clearFocus: () -> Unit,
    onValueChange: (String) -> Unit
) {
    RoundedTextField(
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            Image(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onValueChange("") }
            )
        },
        value = query,
        onValueChange = {
            onValueChange(it)
        },
        maxLength = 100,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = { clearFocus() }
        )
    )
}

@Composable
private fun AddressList(
    query: String,
    addressList: List<AddressInfo>,
    onClickAddress: (AddressInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
    ) {
        itemsIndexed(addressList, key = { _, item -> "${item.roadAddress}|${item.title}|${item.latitude}|${item.longitude}" }) { index, item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{ onClickAddress(item) }
            ) {
                PlaceSearchResultItem(
                    addressInput = query,
                    item = item
                )

                if (index < addressList.lastIndex) {
                    HorizontalDivider(thickness = (0.5).dp, modifier = Modifier.fillMaxWidth(), color = Gray800)
                }
            }
        }
    }
}