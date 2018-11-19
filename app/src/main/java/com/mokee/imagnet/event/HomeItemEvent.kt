package com.mokee.imagnet.event

import com.mokee.imagnet.model.AliItem
import com.mokee.imagnet.model.BtdbMeItem
import com.mokee.imagnet.model.CilicatItem
import com.mokee.imagnet.model.NimaItem

sealed class HomeItemEvent

data class NimaHomeItemEvent(val item: NimaItem) : HomeItemEvent()
data class CilicatHomeItemEvent(val item: CilicatItem) : HomeItemEvent()
data class AliHomeItemEvent(val item: AliItem) : HomeItemEvent()
data class BtdbMeHomeItemEvent(val item: BtdbMeItem) : HomeItemEvent()