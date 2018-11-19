package com.mokee.imagnet.event

import com.mokee.imagnet.model.*

sealed class DetailItemEvent

data class NimaDetailItemEvent(val item: NimaItemDetail) : DetailItemEvent()
data class CilicatDetailItemEvent(val item: CilicatItemDetail) : DetailItemEvent()
data class CilicatSearchDetailEvent(val item: CilicatSearchDetail) : DetailItemEvent()
data class AliDetailItemEvent(val item: AliItemDetail) : DetailItemEvent()
data class BtdbMeDetailItemEvent(val item: BtdbMeDetailItem) : DetailItemEvent()