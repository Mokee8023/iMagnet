package com.mokee.imagnet.presenter.process.nima

import android.text.TextUtils
import com.mokee.imagnet.event.NimaDetailItemEvent
import com.mokee.imagnet.model.NimaFuli
import com.mokee.imagnet.model.NimaItemDetail
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber

object NimaDetailProcess : ProcessResponse() {
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            NimaDetailProcess.process(it.string())
        }?: Timber.e("Nima details response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val itemList = arrayListOf<NimaItemDetail>()

        val document = Jsoup.parse(htmlContent)
        val containerElements = document.getElementsByClass(ITEM_DETAIL_CONTAINER)
        if (containerElements.size > 0) {
            val container = containerElements[1]
            val tableElements = container.getElementsByTag(ITEM_DETAIL_CONTAINER_TABLE)
            if (tableElements.size > 0) {
                val tables = tableElements[0].getElementsByTag(ITEM_DETAIL_CONTAINER_TABLE_TD)

                var title = ""
                var keyWords: MutableList<String> = mutableListOf()
                var fileSize = ""
                var lastActive = ""
                var activeHot = ""
                var magnet = ""
                var thunder = ""
                var fileList: MutableList<String> = mutableListOf()
                var fuliList: MutableList<NimaFuli> = mutableListOf()

                (0..tables.size).forEach table@ {
                    when (it) {
                        0 -> {
                            keyWords = tables[it].text().split(" ").toMutableList()
                            if (keyWords.size > 0) {
                                title = keyWords[0]
                            }
                            keyWords.forEach {key ->
                                if(!key[0].isDigit() && !key[0].isWhitespace()) {
                                    title = key
                                    return@table
                                }
                            }
                        }
                        1 -> {
                            fileSize = tables[it].text()
                        }
                        2 -> {
                            lastActive = tables[it].text()
                        }
                        3 -> {
                            activeHot = tables[it].text()
                        }
                        4 -> {
                            val hrefTag = tables[it].getElementsByTag("a")
                            if (hrefTag.size > 0) {
                                magnet = hrefTag[0].attr("href")
                            }
                        }
                        5 -> {
                            val hrefTag = tables[it].getElementsByTag("a")
                            if (hrefTag.size > 0) {
                                thunder = hrefTag[0].attr("href")
                            }
                        }
                    }
                }

                // extra file list
                val fileListElements = container.getElementsByClass(ITEM_DETAIL_CONTAINER_FILES)
                if (fileListElements.size >= 3) {
                    val fileListItems = fileListElements[2].getElementsByTag(ITEM_DETAIL_CONTAINER_FILES_LI)
                    fileListItems.forEach { file ->
                        fileList.add(file.text())
                    }

                    val fuliItems = fileListElements[1].getElementsByTag(ITEM_DETAIL_CONTAINER_FILES_A)
                    fuliItems.forEach { item ->
                        val fuliHref = item.attr("href")
                        val fuliTitle = item.text()

                        if (!TextUtils.isEmpty(fuliHref) && !TextUtils.isEmpty(fuliTitle)) {
                            fuliList.add(NimaFuli(fuliHref, fuliTitle))
                        }
                    }
                }

                // Add to list
                itemList.add(NimaItemDetail(title, keyWords, fileSize, lastActive, activeHot, magnet, thunder, fileList,  fuliList))
            } else {
                Timber.e("Detail container table from nima item details response is null or empty.")
            }

            Timber.d("The current page item's length is ${itemList.size}")
            if (itemList.size > 0) {
                EventBus.getDefault().post(NimaDetailItemEvent(itemList[0]))
            }
        }
    }

    private const val ITEM_DETAIL_CONTAINER = "container"
    private const val ITEM_DETAIL_CONTAINER_TABLE= "table"
    private const val ITEM_DETAIL_CONTAINER_TABLE_TD = "td"

    private const val ITEM_DETAIL_CONTAINER_FILES = "files"
    private const val ITEM_DETAIL_CONTAINER_FILES_LI = "li"
    private const val ITEM_DETAIL_CONTAINER_FILES_A = "a"
}