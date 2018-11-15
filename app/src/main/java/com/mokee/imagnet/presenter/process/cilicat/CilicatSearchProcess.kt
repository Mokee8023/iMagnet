package com.mokee.imagnet.presenter.process.cilicat

import android.text.TextUtils
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.event.CilicatSearchEvent
import com.mokee.imagnet.event.NimaSearchEvent
import com.mokee.imagnet.model.CilicatSearchItem
import com.mokee.imagnet.model.CilicatSearchItemAttrs
import com.mokee.imagnet.model.NimaItem
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber

object CilicatSearchProcess : ProcessResponse() {
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            CilicatSearchProcess.process(it.string())
        }?: Timber.e("Cilicat search response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val document = Jsoup.parse(htmlContent)
        val tableElements = document.getElementsByAttributeValueStarting("class", ITEM_TABLE_CALSS_NAME)
        tableElements.forEach { table ->
            var title = ""
            var href = ""
            var fileSize = "未知"
            var fileCount = "未知"
            var createTime = "未知"

            val titleClass = table.getElementsByAttributeValueStarting("class", ITEM_TITLE_CLASS_NAME)
            if(titleClass.size > 0) {
                title = titleClass[0].text()
                href = titleClass[0].attr("href")
                if(!TextUtils.isEmpty(href) && !href.contains("baidu")) {
                    href = MagnetConstrant.CILICAT_HOME_URL + href
                }
            }

            val attrClass = table.getElementsByAttributeValueStarting("class", ITEM_ATTR_CLASS_NAME)
            if(attrClass.size > 0) {
                attrClass[0].textNodes().forEach {
                    val text = it.text()
                    if (!text.startsWith("-")) {
                        when {
                            Regex("[a-z]+", RegexOption.IGNORE_CASE).containsMatchIn(text) -> fileSize = text
                            text.contains("-") -> createTime = text
                            else -> fileCount = text
                        }
                    }
                }
            }

            if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(href)) {
                Timber.d("fileSize:$fileSize, fileCount:$fileCount, createTime:$createTime")
                EventBus.getDefault().post(
                        CilicatSearchEvent(
                                CilicatSearchItem(title, href, CilicatSearchItemAttrs(
                                                    fileSize, fileCount, createTime))))
            }
        }
    }

    private const val ITEM_TABLE_CALSS_NAME = "Search__result"
    private const val ITEM_TITLE_CLASS_NAME = "Search__result_title"
    private const val ITEM_ATTR_CLASS_NAME = "Search__result_information"
}