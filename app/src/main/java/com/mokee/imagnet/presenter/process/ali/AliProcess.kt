package com.mokee.imagnet.presenter.process.ali

import com.mokee.imagnet.event.AnalysisFailEvent
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.AliHomeItem
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.presenter.NetworkPresenter
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber

object AliProcess : ProcessResponse() {
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            AliProcess.process(it.string())
        }?: Timber.e("Ali response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val homeItemList = arrayListOf<AliHomeItem>()

        val document = Jsoup.parse(htmlContent)
        val boxElements = document.getElementsByClass(HOME_BOX_CLASS_NAME)
        if(boxElements.size > 0) {
            val box = boxElements[0]
            val ddTags = box.getElementsByTag(HOME_BOX_HOME_LIST_TAG)
            if(ddTags.size > 0) {
                val ddTag = ddTags[0]
                val items = ddTag.getElementsByTag(HOME_BOX_HOME_LIST_ITEM_TAG)
                items.forEach {
                    val href = it.attr("href")
                    val name = it.text()
                    Timber.d("Ali home item, href: $href, name: $name")

                    homeItemList.add(AliHomeItem(href, name))
                }
            }
        } else {
            Timber.e("Can't get body class from ali response.")
            EventBus.getDefault().post(AnalysisFailEvent("Can't analysis ali html content."))
        }

        if(homeItemList.size > 0) {
            homeItemList.forEach {
                NetworkPresenter.instance.getHtmlContent(NetworkPresenter.NetworkItem(RequestType.ALI_ITEM, it.href))
            }
        }
    }

    private const val HOME_BOX_CLASS_NAME = "dlbox hotwords"
    private const val HOME_BOX_HOME_LIST_TAG = "dd"
    private const val HOME_BOX_HOME_LIST_ITEM_TAG = "a"
}