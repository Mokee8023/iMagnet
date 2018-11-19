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

data class CilicatSearchItem(val title: String, val url: String, val attrs: CilicatSearchItemAttrs) : Item()
data class CilicatSearchItemAttrs(val fileSize: String, val fileCount: String, val createTime: String)

data class CilicatSearchDetail(
        val title: String,
        val magnet: String,
        val hash: String,
        val fileCount: String,
        val fileSize: String,
        val receivedTime: String,
        val downloadSpeed: String,
        val fileList: List<String>,
        val recentList: List<CilicatRecentItem>
)

data class CilicatRecentItem(val href: String, val text: String)

data class AliItem(val title: String, val url: String, val attrs: ArrayList<String>, val magnetUrl: String, val torrentUrl: String) : Item()
data class AliItemDetail(
        val title: String,
        val hash: String,
        val fileCount: String,
        val fileSize: String,
        val acceptTime: String,
        val hasDownload: String,
        val downloadSpeed: String,
        val recentDownload: String,
        val magnet: String,
        val fileList: ArrayList<AliFileList>
)

data class AliFileList(
        val name: String,
        val size: String) : Item()

data class BtdbMeItem(
        val title: String,
        val url: String,
        val size: String,
        val fileCount: String,
        val createTime: String,
        val hot: String) : Item()

data class BtdbMeDetailItem(
        val title: String,
        val attributes: ArrayList<String>,
        val magnet: String,
        val thunder: String,
        val fileList: ArrayList<BtdbMeDetailFile>) : Item()

data class BtdbMeDetailFile(
        val name: String,
        val size: String) : Item()