package com.mokee.imagnet.event

import com.mokee.imagnet.model.NimaItemDetail

sealed class DetailItemEvent

data class NimaDetailItemEvent(val item: NimaItemDetail) : DetailItemEvent()