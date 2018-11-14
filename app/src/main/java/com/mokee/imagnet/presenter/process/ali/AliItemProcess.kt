package com.mokee.imagnet.presenter.process.ali

import android.text.TextUtils
import com.mokee.imagnet.event.AliHomeItemEvent
import com.mokee.imagnet.model.AliItem
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber
import java.util.*

object AliItemProcess : ProcessResponse(){
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            process(it.string())
        }?: Timber.e("Ali item response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val itemList = LinkedList<AliItem>()

        val document = Jsoup.parse(htmlContent)
        val tableElements = document.getElementsByClass(TABLE_CLASS_NAME)
        if(tableElements.size > 0) {
            val table = tableElements[0]
            val items = table.getElementsByClass(ITEM_CLASS_NAME)
            // Get all item at current page.
            items.forEach { item ->
                var href = ""
                var title = ""
                val attrs = arrayListOf<String>()

                // Get title and href
                val titles = item.getElementsByTag(ITEM_TITLE_TAG)
                if(titles.size > 0) {
                    val ts = titles[0].getElementsByTag(ITEM_TITLE_TAG_TAG)
                    if(ts.size > 0) {
                        href = ts[0].attr("href")
                        title = ts[0].text()
                    }
                } else {
                    Timber.d("Ali item titles is empty.")
                }

                // Get attributes
                val attrsElements = item.getElementsByClass(ITEM_ATTR_CLASS_NAME)
                if(attrsElements.size > 0) {
                    val spans = attrsElements[0].getElementsByTag(ITEM_ATTR_CLASS_TAG)
                    spans.forEach {
                        attrs.add(it.text())
                    }
                } else {
                    Timber.d("Ali item tails is empty.")
                }

                if(!TextUtils.isEmpty(href) && !TextUtils.isEmpty(title) && attrs.size > 0) {
                    itemList.add(AliItem(title, href, attrs))
                } else {
                    Timber.d("Ali item all attr has one empty.")
                }
            }
        } else {
            Timber.e("Can't get table class from ali item response.")
        }

        Timber.d("The current page item's length is ${itemList.size}")
        if(itemList.size > 0) {
            EventBus.getDefault().post(AliHomeItemEvent(itemList[0]))
        }
    }

    private const val TABLE_CLASS_NAME = "list-area"
    private const val ITEM_CLASS_NAME = "item"

    private const val ITEM_TITLE_TAG = "dt"
    private const val ITEM_TITLE_TAG_TAG = "a"
    private const val ITEM_ATTR_CLASS_NAME = "attr"
    private const val ITEM_ATTR_CLASS_TAG = "span"
}