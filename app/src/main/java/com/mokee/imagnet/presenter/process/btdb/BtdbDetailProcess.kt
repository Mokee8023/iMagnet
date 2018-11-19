package com.mokee.imagnet.presenter.process.btdb

import android.text.TextUtils
import com.mokee.imagnet.event.BtdbMeDetailItemEvent
import com.mokee.imagnet.event.CilicatDetailItemEvent
import com.mokee.imagnet.event.NimaDetailItemEvent
import com.mokee.imagnet.model.*
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import org.w3c.dom.Element
import org.w3c.dom.Text
import timber.log.Timber
import java.lang.Exception

object BtdbDetailProcess : ProcessResponse() {
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            BtdbDetailProcess.process(it.string())
        }?: Timber.e("Cilicat details response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val document = Jsoup.parse(htmlContent)
        val mainElements = document.getElementsByClass(ITEM_DETAIL_MAIN_CLASS)
        if (mainElements.size > 0) {
            var title = ""
            val attributes = arrayListOf<String>()
            var magnet = ""
            var thunder = ""
            val fileList = arrayListOf<BtdbMeDetailFile>()

            val main = mainElements[0]

            val titleElements = main.getElementsByClass(ITEM_DETAIL_TITLE_CALSS)
            if(titleElements.size > 0) {
                title = titleElements[0].text()
            }

            val attributesElements = main.getElementsByClass(ITEM_DETAIL_MAIN_ATTRIBUTE_CLASS)
            if(attributesElements.size > 0) {
                val attrsElements = attributesElements[0].getElementsByTag(ITEM_DETAIL_MAIN_ATTRIBUTE_TAG)
                (0 until attrsElements.size).forEach {attrIndex ->
                    if(attrIndex < 5) {
                        attributes.add(attrsElements[attrIndex].text())
                    } else {
                        if(attrIndex == 5) {
                            val ms = attrsElements[attrIndex].getElementsByTag("a")
                            if(ms.size > 0) {
                                magnet = ms[0].attr("href")
                            }
                        } else if(attrIndex == 6) {
                            val ms = attrsElements[attrIndex].getElementsByTag("a")
                            if(ms.size > 0) {
                                magnet = ms[0].attr("href")
                            }
                        }
                    }
                }
            }

            val fileListElements = main.getElementsByClass(ITEM_DETAIL_MAIN_FILE_LIST_CLASS)
            if(fileListElements.size > 0) {
                val files = fileListElements[0].getElementsByTag(ITEM_DETAIL_MAIN_FILE_LIST_TAG)
                files.forEach {file ->
                    var size = ""
                    var name = ""
                    name = (file.childNodes()[0] as TextNode).text().trim()
                    val sizes = file.getElementsByTag("span")
                    if(sizes.size > 0) {
                        size = sizes[0].text()
                    }

                    if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(size)) {
                        fileList.add(BtdbMeDetailFile(name, size))
                    }
                }
            }

            if(!TextUtils.isEmpty(title) && attributes.size > 0) {
                EventBus.getDefault().post(BtdbMeDetailItemEvent(BtdbMeDetailItem(title, attributes, magnet, thunder, fileList)))
            }
        }
    }

    private const val ITEM_DETAIL_MAIN_CLASS = "main"
    private const val ITEM_DETAIL_TITLE_CALSS = "T1"
    private const val ITEM_DETAIL_MAIN_ATTRIBUTE_CLASS = "BotInfo"
    private const val ITEM_DETAIL_MAIN_ATTRIBUTE_TAG = "p"
    private const val ITEM_DETAIL_MAIN_FILE_LIST_CLASS = "flist"
    private const val ITEM_DETAIL_MAIN_FILE_LIST_TAG = "li"

}