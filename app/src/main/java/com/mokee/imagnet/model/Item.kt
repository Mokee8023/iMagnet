package com.mokee.imagnet.model

sealed class Item

data class NimaItem(val title: String, val url: String, val detail: String, val magnet: String) : Item()

data class CilicatItem(val title: String, val url: String, val detail: String, val magnet: String) : Item()