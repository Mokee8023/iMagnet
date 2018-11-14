package com.mokee.imagnet.model

sealed class HomeItem

data class NimaHomeItem(val href: String, val name: String) : HomeItem()

data class CilicatHomeItem(val href: String, val name: String) : HomeItem()

data class AliHomeItem(val href: String, val name: String) : HomeItem()