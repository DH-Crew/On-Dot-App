package com.ondot.ui.model

import com.ondot.domain.model.enums.TransportType
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_car
import ondot.core.design_system.generated.resources.ic_public_transport
import org.jetbrains.compose.resources.DrawableResource

data class TransportInfo(
    val title: String,
    val icon: DrawableResource,
)

fun TransportType.toTransportInfo(): TransportInfo =
    TransportInfo(
        title = if (this == TransportType.PUBLIC_TRANSPORT) "대중교통 이용" else "자가용 이용",
        icon =
            if (this == TransportType.PUBLIC_TRANSPORT) Res.drawable.ic_public_transport
            else Res.drawable.ic_car
    )
