package com.mokee.imagnet.presenter.process.cilicat

import android.text.TextUtils
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.constrant.MagnetConstrant.CILICAT_HOME_URL
import com.mokee.imagnet.event.CilicatHomeItemEvent
import com.mokee.imagnet.event.NimaHomeItemEvent
import com.mokee.imagnet.model.NimaItem
import com.mokee.imagnet.fragment.NiMaFragment
import com.mokee.imagnet.model.CilicatItem
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber
import java.util.*

object CilicatItemProcess : ProcessResponse(){
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            process(it.string())
        }?: Timber.e("Cilicat item response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val document = Jsoup.parse(htmlContent)
        val cardElement = document.getElementsByAttributeValueStarting("id", ITEM_CARD_ID)
        if(null != cardElement && cardElement.size > 0) {
            var title = ""
            var href = ""
            var imageUrl = ""
            val detailArray = arrayListOf<String>()

            val titleElements = cardElement[0].getElementsByAttributeValueStarting("class", ITEM_TITLE_CLASS_NAME)
            if(titleElements.size > 0) {
                val text = titleElements[0].text()
                href = titleElements[0].attr("href")
                if(!TextUtils.isEmpty(href)) {
                    href = CILICAT_HOME_URL + href
                }
                title = text.substring(0, text.lastIndexOf("_"))
            }

            val cardDetailElements = cardElement[0].getElementsByAttributeValueStarting("class", ITEM_CARD_DETAIL_CLASS_NAME)
            if(cardDetailElements.size > 0) {
                val imageElements = cardDetailElements[0].getElementsByAttributeValueStarting("class", ITEM_CARD_DETAIL_IMAGE_CLASS_NAME)
                if(imageElements.size > 0) {
                    val img = imageElements[0].getElementsByTag(ITEM_CARD_DETAIL_IMAGE_TAG)
                    if(img.size > 0) {
                        imageUrl = img[0].attr("src")
                        if(!imageUrl.startsWith("http:")) {
                            imageUrl = "http:$imageUrl"
                        }
                    }
                }

                val detailElements = cardDetailElements[0].getElementsByAttributeValueStarting("class", ITEM_CARD_DETAIL_DETAIL_CLASS_NAME)
                if(detailElements.size > 0) {
                    val details = detailElements[0].getElementsByTag(ITEM_CARD_DETAIL_DETAIL_TAG)
                    if(details.size > 0) {
                        (1 until details.size - 1).forEach {
                            val text = details[it].text()
                            if(!TextUtils.isEmpty(text) && !detailArray.contains(text)) {
                                detailArray.add(text.replace("更多>>", ""))
                            }
                        }
                    }
                }
            }

            if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(imageUrl) && detailArray.size > 0 && !TextUtils.isEmpty(href)) {
                EventBus.getDefault().post(CilicatHomeItemEvent(CilicatItem(title, href, imageUrl, detailArray)))
            }
        }
    }

    private const val ITEM_CARD_ID = "MovieCard__work_card"
    private const val ITEM_TITLE_CLASS_NAME = "MovieCard__title"
    private const val ITEM_CARD_DETAIL_CLASS_NAME = "MovieCard__wrapper"
    private const val ITEM_CARD_DETAIL_IMAGE_CLASS_NAME = "MovieCard__cover"
    private const val ITEM_CARD_DETAIL_DETAIL_CLASS_NAME = "MovieCard__content"

    private const val ITEM_CARD_DETAIL_IMAGE_TAG = "img"
    private const val ITEM_CARD_DETAIL_DETAIL_TAG = "div"
}