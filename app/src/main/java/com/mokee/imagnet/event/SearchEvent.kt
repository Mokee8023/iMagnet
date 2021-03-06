package com.mokee.imagnet.event

import com.mokee.imagnet.model.BtdbMeItem
import com.mokee.imagnet.model.CilicatSearchItem
import com.mokee.imagnet.model.NimaItem

sealed class SearchEvent

data class NimaSearchEvent(val item: NimaItem) : SearchEvent()
data class CilicatSearchEvent(val item: CilicatSearchItem) : SearchEvent()
data class BtdbMeSearchEvent(val item: BtdbMeItem) : SearchEvent()