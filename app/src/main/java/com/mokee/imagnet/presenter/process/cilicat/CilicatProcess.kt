package com.mokee.imagnet.presenter.process.cilicat

import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.event.AnalysisFailEvent
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.model.CilicatHomeItem
import com.mokee.imagnet.presenter.NetworkPresenter
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber

object CilicatProcess : ProcessResponse() {
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            process(it.string())
        }?: Timber.e("Cilicat response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val homeItemList = arrayListOf<CilicatHomeItem>()

        val document = Jsoup.parse(htmlContent)
        val hotElements = document.getElementsByAttributeValueStarting("class", HOME_DIV_CLASS_NAME)
        if(hotElements.size > 0) {
            val element = hotElements[0]
            val aTags = element.getElementsByTag(HOME_DIV_HOT_TAG_NAME)
            aTags.forEach {
                var href = it.attr("href")
                href = MagnetConstrant.CILICAT_HOME_URL + href

                val name = it.text()
                Timber.d("Cilicat home item, href: $href, name: $name")

                homeItemList.add(CilicatHomeItem(href, name))
            }
        } else {
            Timber.e("Can't get body class from cilicat response.")
            EventBus.getDefault().post(AnalysisFailEvent("Can't analysis Cilicat html content."))
        }

        if(homeItemList.size > 0) {
            homeItemList.forEach {
                NetworkPresenter.instance?.getHtmlContent(NetworkPresenter.NetworkItem(RequestType.CILICAT_ITEM, it.href))
            }
        }
    }

    private const val HOME_DIV_CLASS_NAME = "Home__search_hot_words"
    private const val HOME_DIV_HOT_TAG_NAME = "a"
}