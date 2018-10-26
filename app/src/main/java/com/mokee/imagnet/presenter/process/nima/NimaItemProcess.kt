package com.mokee.imagnet.presenter.process.nima

import android.text.TextUtils
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.event.NimaHomeItemEvent
import com.mokee.imagnet.model.NimaItem
import com.mokee.imagnet.fragment.NiMaFragment
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber
import java.util.*

object NimaItemProcess : ProcessResponse(){
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            process(it.string())
        }?: Timber.e("Nima item response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val itemList = LinkedList<NimaItem>()

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
                    Timber.d("Nima item titles is empty.")
                }

                // Get details
                val tails = item.getElementsByClass(ITEM_TAIL_CALSS_NAME)
                if(tails.size > 0) {
                    detail = tails[0].text().replace("Magnet", "", true)
                    val tailTag = tails[0].getElementsByTag("a")
                    if(tailTag.size > 0) {
                        magnet = tailTag[0].attr("href")
                    } else {
                        Timber.d("Nima item tails tag is empty.")
                    }
                } else {
                    Timber.d("Nima item tails is empty.")
                }

                if(!TextUtils.isEmpty(href) &&
                        !TextUtils.isEmpty(title) &&
                        !TextUtils.isEmpty(detail) &&
                        !TextUtils.isEmpty(magnet)) {
                    itemList.add(NimaItem(title, href, detail, magnet))
//                    Timber.d("Nima item: ${itemList[itemList.size - 1]}")
                } else {
                    Timber.d("Nima item all attr has one empty.")
                }
            }
        } else {
            Timber.e("Can't get table class from nima item response.")
        }

        Timber.d("The current page item's length is ${itemList.size}")
        if(itemList.size > 0) {
            EventBus.getDefault().post(NimaHomeItemEvent(itemList[0]))
        }
    }

    private const val ITEM_TABLE_CALSS_NAME = "table"
    private const val ITEM_CLASS_NAME = "x-item"

    private const val ITEM_TITLE_CALSS_NAME = "title"
    private const val ITEM_TAIL_CALSS_NAME = "tail"
}