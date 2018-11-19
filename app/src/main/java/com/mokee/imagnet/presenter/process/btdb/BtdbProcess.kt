package com.mokee.imagnet.presenter.process.btdb

import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.event.AnalysisFailEvent
import com.mokee.imagnet.model.BtdbMeHomeItem
import com.mokee.imagnet.model.CilicatHomeItem
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.presenter.NetworkPresenter
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber

object BtdbProcess : ProcessResponse() {
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            BtdbProcess.process(it.string())
        }?: Timber.e("Btdb me response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val homeItemList = arrayListOf<BtdbMeHomeItem>()

        val document = Jsoup.parse(htmlContent)
        val hotElements = document.getElementsByAttributeValueStarting("class", HOME_DIV_CLASS_NAME)
        if(hotElements.size > 0) {
            val element = hotElements[0]
            val aTags = element.getElementsByTag(HOME_DIV_HOT_TAG_NAME)
            aTags.forEach {
                var href = it.attr("href")
                href = MagnetConstrant.BTDB_ME_HOME_URL + href

                val name = it.text()
                Timber.d("Btdb me home item, href: $href, name: $name")

                homeItemList.add(BtdbMeHomeItem(href, name))
            }
        } else {
            Timber.e("Can't get body class from btdb me response.")
            EventBus.getDefault().post(AnalysisFailEvent("Can't analysis btdb me html content."))
        }

        if(homeItemList.size > 0) {
            homeItemList.forEach {
                NetworkPresenter.instance?.getHtmlContent(NetworkPresenter.NetworkItem(RequestType.BTDB_ME_ITEM, it.href))
            }
        }
    }

    private const val HOME_DIV_CLASS_NAME = "hotwords"
    private const val HOME_DIV_HOT_TAG_NAME = "a"
}