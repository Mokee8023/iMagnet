package com.mokee.imagnet.event

import com.mokee.imagnet.model.CilicatItemDetail
import com.mokee.imagnet.model.CilicatSearchDetail
import com.mokee.imagnet.model.NimaItemDetail

sealed class DetailItemEvent

data class NimaDetailItemEvent(val item: NimaItemDetail) : DetailItemEvent()
data class CilicatDetailItemEvent(val item: CilicatItemDetail) : DetailItemEvent()
data class CilicatSearchDetailEvent(val item: CilicatSearchDetail) : DetailItemEvent()