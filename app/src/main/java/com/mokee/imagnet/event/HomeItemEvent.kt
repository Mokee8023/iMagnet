package com.mokee.imagnet.event

import com.mokee.imagnet.model.NimaItem

sealed class HomeItemEvent

data class NimaHomeItemEvent(val item: NimaItem) : HomeItemEvent()