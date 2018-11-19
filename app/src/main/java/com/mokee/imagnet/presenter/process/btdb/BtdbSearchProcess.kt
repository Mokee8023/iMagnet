package com.mokee.imagnet.presenter.process.btdb

import android.text.TextUtils
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.event.BtdbMeSearchEvent
import com.mokee.imagnet.event.BtdbSearchFailEvent
import com.mokee.imagnet.model.BtdbMeItem
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber

object BtdbSearchProcess : ProcessResponse(){
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            process(it.string())
        }?: Timber.e("Btdb me item response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val document = Jsoup.parse(htmlContent)
        val listElements = document.getElementsByClass(ITEM_LIST_CLASS)
        if(null != listElements && listElements.size > 0) {
            val lis = listElements[0].getElementsByTag(ITEM_LIST_ITEM_TAG)
            lis.forEach {
                var title: String = ""
                var href: String = ""
                var size: String = ""
                var fileCount: String = ""
                var createTime: String = ""
                var hot: String = ""

                val titleClass = it.getElementsByClass(ITEM_LIST_ITEM_TITLE_CLASS_NAME)
                if(titleClass.size > 0) {
                    val titleTag = titleClass[0].getElementsByTag(ITEM_LIST_ITEM_TITLE_TAG)
                    if(titleTag.size > 0) {
                        title = titleTag[0].attr("title")
                        href = MagnetConstrant.BTDB_ME_HOME_URL + titleTag[0].attr("href")
                    }
                }

                val attrsClass = it.getElementsByClass(ITEM_LIST_ITEM_ATTR_CLASS_NAME)
                if(attrsClass.size > 0) {
                    val attrsTag = attrsClass[0].getElementsByTag(ITEM_LIST_ITEM_ATTR_TAG)
                    (0 until attrsTag.size).forEach { attrIndex ->
                        when(attrIndex) {
                            0 -> size = attrsTag[attrIndex].text()
                            1 -> fileCount = attrsTag[attrIndex].text()
                            2 -> createTime = attrsTag[attrIndex].text()
                            3 -> hot = attrsTag[attrIndex].text()
                        }
                    }
                }

                if(
                        !TextUtils.isEmpty(title) &&
                        !TextUtils.isEmpty(href) &&
                        !TextUtils.isEmpty(size) &&
                        !TextUtils.isEmpty(fileCount) &&
                        !TextUtils.isEmpty(createTime) &&
                        !TextUtils.isEmpty(hot)) {
                    EventBus.getDefault().post(
                            BtdbMeSearchEvent(
                                    BtdbMeItem(title, href, size, fileCount, createTime, hot)))
                }
            }
        } else {
            Timber.e("Can't get body class from btdb me response.")
            EventBus.getDefault().post(BtdbSearchFailEvent("Can't analysis btdb me html content."))
        }
    }

    private const val ITEM_LIST_CLASS = "mlist"
    private const val ITEM_LIST_ITEM_TAG = "li"

    private const val ITEM_LIST_ITEM_TITLE_CLASS_NAME = "T1"
    private const val ITEM_LIST_ITEM_TITLE_TAG = "a"

    private const val ITEM_LIST_ITEM_ATTR_CLASS_NAME = "BotInfo"
    private const val ITEM_LIST_ITEM_ATTR_TAG = "span"
}