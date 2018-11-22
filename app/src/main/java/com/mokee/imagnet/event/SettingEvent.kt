package com.mokee.imagnet.event

sealed class SettingEvent

data class TabChangeEvent(val selectedIndex: Set<String>): SettingEvent()