package com.mokee.imagnet.presenter.process.nima

import android.text.TextUtils
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.event.NimaHomeItemEvent
import com.mokee.imagnet.event.NimaSearchEvent
import com.mokee.imagnet.model.NimaItem
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber
import java.util.*

object NimaSearchProcess : ProcessResponse() {
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            NimaSearchProcess.process(it.string())
        }?: Timber.e("Nima search response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val document = Jsoup.parse(htmlContent)
        val tableElements = document.getElementsByClass(ITEM_TABLE_CALSS_NAME)
        if(tableElements.size > 0) {
            val table = tableElements[0]
            val items = table.getElementsByClass(ITEM_CLASS_NAME)
            // Get all item at current page.
            items.forEach { item ->
                var href = ""
                var title = ""
                var detail = ""
                var magnet = ""

                // Get title and href
                val titles = item.getElementsByClass(ITEM_TITLE_CALSS_NAME)
                if(titles.size > 0) {
                    href = MagnetConstrant.NIMA_HOME_URL + titles[0].attr("href")
                    title = titles[0].text()
                } else {
                    Timber.d("Nima search item titles is empty.")
                }

                // Get details
                val tails = item.getElementsByClass(ITEM_TAIL_CALSS_NAME)
                if(tails.size > 0) {
                    detail = tails[0].text().replace("Magnet", "", true)
                    val tailTag = tails[0].getElementsByTag("a")
                    if(tailTag.size > 0) {
                        magnet = tailTag[0].attr("href")
                    } else {
                        Timber.d("Nima search item tails tag is empty.")
                    }
                } else {
                    Timber.d("Nima search item tails is empty.")
                }

                if(!TextUtils.isEmpty(href) &&
                        !TextUtils.isEmpty(title) &&
                        !TextUtils.isEmpty(detail) &&
                        !TextUtils.isEmpty(magnet)) {
                    EventBus.getDefault().post(NimaSearchEvent(NimaItem(title, href, detail, magnet)))
                } else {
                    Timber.d("Nima search item all attr has one empty.")
                }
            }
        } else {
            Timber.e("Can't get table class from nima item response.")
        }
    }

    private const val ITEM_TABLE_CALSS_NAME = "table"
    private const val ITEM_CLASS_NAME = "x-item"

    private const val ITEM_TITLE_CALSS_NAME = "title"
    private const val ITEM_TAIL_CALSS_NAME = "tail"
}