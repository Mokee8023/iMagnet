package com.mokee.imagnet.presenter.process.cilicat

import android.text.TextUtils
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.event.CilicatSearchDetailEvent
import com.mokee.imagnet.event.CilicatSearchEvent
import com.mokee.imagnet.event.NimaSearchEvent
import com.mokee.imagnet.model.*
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber

object CilicatSearchDetailProcess : ProcessResponse() {
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            CilicatSearchDetailProcess.process(it.string())
        }?: Timber.e("Cilicat search detail response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val document = Jsoup.parse(htmlContent)
        val magnetElements = document.getElementsByAttributeValueStarting("class", ITEM_MAGNET_LINK)
        if(magnetElements.size > 0) {
            var title = ""
            val magnetHref = magnetElements[0].attr("href")
            var hash = ""
            var fileCount = ""
            var fileSize = ""
            var receivedTime = ""
            var downloadSpeed = ""

            val titleElement = document.getElementsByAttributeValueStarting("id", ITEM_TITLE_ID)
            if(null != titleElement) {
                title = titleElement.text()
            }

            val fileList = arrayListOf<String>()
            val recentList = arrayListOf<CilicatRecentItem>()

            val attrsElements = document.getElementsByAttributeValueStarting("class", ITEM_DETAIL)
            if(attrsElements.size > 0) {
                val attrs = attrsElements[0].getElementsByTag(ITEM_DETAIL_TAG)
                (0 until attrs.size).forEach {
                    when(it) {
                        0 -> hash = "HASH:" + attrs[it].text()
                        1 -> fileCount = "文件数量：" + attrs[it].text()
                        2 -> fileSize = "文件大小：" + attrs[it].text()
                        3 -> receivedTime = "收录时间：" + attrs[it].text()
                        4 -> downloadSpeed = "下载速度：" + attrs[it].text()
                    }
                }
            }

            val fileListElements = document.getElementsByAttributeValueStarting("class", ITEM_DETAIL_FILE_LIST_CLASS)
            if(fileListElements.size > 0) {
                val files = fileListElements[0].getElementsByTag(ITEM_DETAIL_FILE_LIST_TAG)
                files.forEach {
                    fileList.add(it.text())
                }
            }

            val recentLists = document.getElementsByAttributeValueStarting("class", ITEM_DETAIL_RECENT_FILE_LIST_CLASS)
            if(recentLists.size > 0) {
                val recentFiles = recentLists[0].getElementsByTag(ITEM_DETAIL_RECENT_FILE_LIST_TAG)
                recentFiles.forEach {
                    var href = it.attr("href")
                    val recentName = it.text()
                    if(!TextUtils.isEmpty(href)) {
                        href = MagnetConstrant.CILICAT_HOME_URL + href
                    }

                    if(!TextUtils.isEmpty(href) && !TextUtils.isEmpty(recentName)) {
                        recentList.add(CilicatRecentItem(href, recentName))
                    }
                }
            }

            if (
                    !TextUtils.isEmpty(magnetHref) &&
                    !TextUtils.isEmpty(title) &&
                    !TextUtils.isEmpty(hash) &&
                    !TextUtils.isEmpty(fileCount) &&
                    !TextUtils.isEmpty(fileSize) &&
                    !TextUtils.isEmpty(receivedTime) &&
                    !TextUtils.isEmpty(downloadSpeed) ) {
                EventBus.getDefault().post(
                        CilicatSearchDetailEvent(
                                CilicatSearchDetail(
                                        title,
                                        magnetHref,
                                        hash,
                                        fileCount,
                                        fileSize,
                                        receivedTime,
                                        downloadSpeed,
                                        fileList,
                                        recentList)))
            }
        }
    }

    private const val ITEM_TITLE_ID = "Information__title"

    private const val ITEM_MAGNET_LINK = "Information__magnet"
    private const val ITEM_DETAIL = "Information__detail_information"
    private const val ITEM_DETAIL_TAG = "b"

    private const val ITEM_DETAIL_FILE_LIST_CLASS = "Information__files_information"
    private const val ITEM_DETAIL_FILE_LIST_TAG = "li"

    private const val ITEM_DETAIL_RECENT_FILE_LIST_CLASS = "Information__recommend_infohash"
    private const val ITEM_DETAIL_RECENT_FILE_LIST_TAG = "a"
}