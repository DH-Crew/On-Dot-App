package com.ondot.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ondot.designsystem.theme.OnDotColor.Gray800
import com.ondot.domain.model.member.AddressInfo

@Composable
fun AddressList(
    modifier: Modifier = Modifier,
    query: String,
    addressList: List<AddressInfo>,
    onClick: (AddressInfo) -> Unit,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
    ) {
        itemsIndexed(addressList, key = {
            _,
            item,
            ->
            "${item.roadAddress}|${item.title}|${item.latitude}|${item.longitude}"
        }) { index, item ->
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onClick(item) },
            ) {
                PlaceSearchResultItem(
                    addressInput = query,
                    item = item,
                )

                if (index < addressList.lastIndex) {
                    HorizontalDivider(thickness = (0.5).dp, modifier = Modifier.fillMaxWidth(), color = Gray800)
                }
            }
        }
    }
}
