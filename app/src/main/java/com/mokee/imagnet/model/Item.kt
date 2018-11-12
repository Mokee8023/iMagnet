package com.mokee.imagnet.model

sealed class Item

data class NimaItem(val title: String, val url: String, val detail: String, val magnet: String) : Item()
data class NimaItemDetail(
        val title: String,
        val keyWords: List<String>,
        val fileSize: String,
        val lastActive: String,
        val activeHot: String,
        val magnet: String,
        val thunder: String,
        val fileList: List<String>,
        val fuliList: List<NimaFuli>
)
data class NimaFuli(val href: String, val text: String) :Item()

data class CilicatItem(val title: String, val url: String, val imageUrl: String, val detailArray: ArrayList<String>) : Item()
data class CilicatItemDetail(
        val title: String,
        val attributes: List<String>,
        val details: String,
        val fileList: List<CilicatFileList>
)

data class CilicatFileList(
        val name: String,
        val magnet: String,
        val size: String,
        val sharpness: String,
        val caption: String,
        val publishTime: String) : Item()