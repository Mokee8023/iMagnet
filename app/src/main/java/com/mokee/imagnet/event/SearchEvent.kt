package com.mokee.imagnet.event

import com.mokee.imagnet.model.NimaItem

sealed class SearchEvent

data class NimaSearchEvent(val item: NimaItem) : SearchEvent()