package com.mokee.imagnet.presenter.process.nima

import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.model.NimaHomeItem
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.presenter.NetworkPresenter
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.jsoup.Jsoup
import timber.log.Timber

object NimaProcess : ProcessResponse() {
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            process(it.string())
        }?: Timber.e("Nima response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val homeItemList = arrayListOf<NimaHomeItem>()

        val document = Jsoup.parse(htmlContent)
        val goodElements = document.getElementsByClass(HOME_DIV_CLASS_NAME)
        if(goodElements.size > 0) {
            val element = goodElements[0]
            val aTags = element.getElementsByTag(HOME_DIV_GOOD_TAG_NAME)
            aTags.forEach {
                var href = it.attr("href")
                href = MagnetConstrant.NIMA_HOME_URL + href

                // special process
//                href = href.replace("hot", "first")

                val name = it.text()
                Timber.d("Nima home item, href: $href, name: $name")

                homeItemList.add(NimaHomeItem(href, name))
            }
        } else {
            Timber.e("Can't get body class from nima response.")
        }

        if(homeItemList.size > 0) {
            homeItemList.forEach {
                NetworkPresenter.instance?.getHtmlContent(NetworkPresenter.NetworkItem(RequestType.NIMA_ITEM, it.href))
            }
        }
    }

    private const val HOME_DIV_CLASS_NAME = "good"
    private const val HOME_DIV_GOOD_TAG_NAME = "a"
}