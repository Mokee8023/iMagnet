package com.mokee.imagnet.presenter.process.ali

import android.text.TextUtils
import com.mokee.imagnet.event.AliDetailItemEvent
import com.mokee.imagnet.event.CilicatDetailItemEvent
import com.mokee.imagnet.event.NimaDetailItemEvent
import com.mokee.imagnet.model.*
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.jsoup.Jsoup
import org.w3c.dom.Element
import org.w3c.dom.Text
import timber.log.Timber
import java.lang.Exception

object AliDetailProcess : ProcessResponse() {
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            AliDetailProcess.process(it.string())
        }?: Timber.e("Ali details response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val document = Jsoup.parse(htmlContent)
        val contentElements = document.getElementsByClass(ITEM_DETAIL_CONTAINER_CLASS)
        if (contentElements.size > 0) {
            var title = ""
            var hash = ""
            var fileCount = ""
            var fileSize = ""
            var acceptTime = ""
            var hasDownload = ""
            var downloadSpeed = ""
            var recentDownload = ""
            var magnet = ""
            val fileList = arrayListOf<AliFileList>()

            val titleElements = contentElements[0].getElementsByTag(ITEM_DETAIL_CONTAINER_TITLE_TAG)
            if(titleElements.size > 0) { title = titleElements[0].text() }

            val detailElements = contentElements[0].getElementsByClass(ITEM_DETAIL_CONTAINER_DETAIL_CLASS)
            if(detailElements.size > 0) {
                val attrsElements = detailElements[0].getElementsByClass(ITEM_DETAIL_CONTAINER_DETAIL_ATTRS_CLASS)
                if(attrsElements.size > 0) {
                    val attrs = attrsElements[0].getElementsByTag(ITEM_DETAIL_CONTAINER_DETAIL_ATTRS_TAG)
                    (0 until attrs.size).forEach {
                        when(it) {
                            0 -> { hash = attrs[it].text() }
                            1 -> { fileCount = attrs[it].text() }
                            2 -> { fileSize = attrs[it].text() }
                            3 -> { acceptTime = attrs[it].text() }
                            4 -> { hasDownload = attrs[it].text() }
                            5 -> { downloadSpeed = attrs[it].text() }
                            6 -> { recentDownload = attrs[it].text() }
                        }
                    }
                }

                val magnetElements = detailElements[0].getElementsByClass(ITEM_DETAIL_CONTAINER_DETAIL_MAGNET_CLASS)
                if(magnetElements.size > 0) {
                    val magnetElement = magnetElements[0].getElementsByTag(ITEM_DETAIL_CONTAINER_DETAIL_MAGNET_TAG)
                    if(magnetElement.size > 0) {
                        magnet = magnetElement[0].attr("href")
                    }
                }

                val fileListElements = detailElements[0].getElementsByClass(ITEM_DETAIL_CONTAINER_DETAIL_FILE_LIST_CLASS)
                if(fileListElements.size > 0) {
                    val listElements = fileListElements[0].getElementsByTag(ITEM_DETAIL_CONTAINER_DETAIL_FILE_LIST_TAG)
                    listElements.forEach {
                        var name = ""
                        var size = ""

                        val names = it.getElementsByClass(ITEM_DETAIL_CONTAINER_DETAIL_FILE_LIST_FILE_NAME_CLASS)
                        if(names.size > 0) {
                            name = names[0].text()
                        }

                        val sizes = it.getElementsByClass(ITEM_DETAIL_CONTAINER_DETAIL_FILE_LIST_SIZE_CLASS)
                        if(sizes.size > 0) {
                            size = sizes[0].text()
                        }

                        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(size)) {
                            fileList.add(AliFileList(name, size))
                        }
                    }
                }
            }

            if(
                    !TextUtils.isEmpty(title) &&
                    !TextUtils.isEmpty(hash) &&
                    !TextUtils.isEmpty(fileCount) &&
                    !TextUtils.isEmpty(fileSize) &&
                    !TextUtils.isEmpty(acceptTime) &&
                    !TextUtils.isEmpty(hasDownload) &&
                    !TextUtils.isEmpty(downloadSpeed) &&
                    !TextUtils.isEmpty(recentDownload) &&
                    !TextUtils.isEmpty(magnet) &&
                    fileList.size > 0
            ) {
                EventBus.getDefault().post(AliDetailItemEvent( AliItemDetail(
                        title, hash, fileCount, fileSize, acceptTime, hasDownload,
                        downloadSpeed, recentDownload, magnet, fileList
                )))
            }
        }
    }

    private const val ITEM_DETAIL_CONTAINER_CLASS = "torrent"
    private const val ITEM_DETAIL_CONTAINER_TITLE_TAG = "h2"

    private const val ITEM_DETAIL_CONTAINER_DETAIL_CLASS = "detail"

    private const val ITEM_DETAIL_CONTAINER_DETAIL_ATTRS_CLASS = "dd desc"
    private const val ITEM_DETAIL_CONTAINER_DETAIL_ATTRS_TAG = "b"

    private const val ITEM_DETAIL_CONTAINER_DETAIL_MAGNET_CLASS = "dd magnet"
    private const val ITEM_DETAIL_CONTAINER_DETAIL_MAGNET_TAG = "a"

    private const val ITEM_DETAIL_CONTAINER_DETAIL_FILE_LIST_CLASS = "dd filelist"
    private const val ITEM_DETAIL_CONTAINER_DETAIL_FILE_LIST_TAG = "p"
    private const val ITEM_DETAIL_CONTAINER_DETAIL_FILE_LIST_FILE_NAME_CLASS = "filename"
    private const val ITEM_DETAIL_CONTAINER_DETAIL_FILE_LIST_SIZE_CLASS = "size"
}