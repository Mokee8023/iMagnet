package com.mokee.imagnet.presenter.process.cilicat

import android.text.TextUtils
import com.mokee.imagnet.event.CilicatDetailItemEvent
import com.mokee.imagnet.event.NimaDetailItemEvent
import com.mokee.imagnet.model.*
import com.mokee.imagnet.presenter.process.ProcessResponse
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.jsoup.Jsoup
import org.w3c.dom.Element
import org.w3c.dom.Text
import timber.log.Timber
import java.lang.Exception

object CilicatDetailProcess : ProcessResponse() {
    override fun processResponse(response: Response) {
        val responseBody = response.body()
        responseBody?.let {
            CilicatDetailProcess.process(it.string())
        }?: Timber.e("Cilicat details response body is null or empty.")
    }

    private fun process(htmlContent: String) {
        val document = Jsoup.parse(htmlContent)
        val contentElements = document.getElementsByAttributeValueStarting("class", ITEM_DETAIL_CONTAINER_CLASS)
        if (contentElements.size > 0) {
            var title = ""
            val attributes = arrayListOf<String>()
            var detail = ""

            val container = contentElements[0]

            val titleElements = container.getElementsByTag(ITEM_DETAIL_TITLE_TAG)
            if(titleElements.size > 0) {
                title = titleElements[0].text()
            }

            val attributesElements = container.getElementsByAttributeValueStarting("class", ITEM_DETAIL_CONTAINER_ATTRIBUTE_CLASS)
            if(attributesElements.size > 0) {
                val leftElements = attributesElements[0].getElementsByAttributeValueStarting("class", ITEM_DETAIL_CONTAINER_ATTRIBUTE_LEFT_CLASS)
                val rightElements = attributesElements[0].getElementsByAttributeValueStarting("class", ITEM_DETAIL_CONTAINER_ATTRIBUTE_RIGHT_CLASS)

                if(leftElements.size > 0) {
                    val leftDetailElements = leftElements[0].getElementsByTag(ITEM_DETAIL_CONTAINER_ATTRIBUTE_TAG)
                    leftDetailElements.forEach {
                        if(!TextUtils.isEmpty(it.text())) {
                            attributes.add(it.text())
                        }
                    }
                }

                if(rightElements.size > 0) {
                    val rightDetailElements = rightElements[0].getElementsByTag(ITEM_DETAIL_CONTAINER_ATTRIBUTE_TAG)
                    rightDetailElements.forEach {
                        if(!TextUtils.isEmpty(it.text())) {
                            attributes.add(it.text())
                        }
                    }
                }

            }

            val detailElements = container.getElementsByAttributeValueStarting("class", ITEM_DETAIL_CONTAINER_DETAILS_CLASS)
            if(detailElements.size > 0) {
                detail = detailElements[0].text()
            }

            val fileList = processFileList(document.head())

            if(!TextUtils.isEmpty(title) && attributes.size > 0) {
                EventBus.getDefault().post(CilicatDetailItemEvent(CilicatItemDetail(title, attributes, detail, fileList)))
            }
        }
    }

    private fun processFileList(head: org.jsoup.nodes.Element) : ArrayList<CilicatFileList> {
        val fileList = arrayListOf<CilicatFileList>()

        val datas = head.getElementsByTag(HEAD_DATA_TAG)
        datas.forEach {
            if(it.childNodeSize() > 0) {
                val data = it.childNode(0).attributes().get("data").toString()
                if(data.contains("var __data")) {
                    try {
                        val startIndex = data.indexOf("{")
                        val endIndex = data.lastIndexOf(";")
                        if(startIndex > 1 && endIndex != -1) {
                            val subData = data.substring(startIndex - 1, endIndex)

                            val dataJson = JSONObject(subData)
                            val resources = dataJson
                                                            .getJSONObject("baike")
                                                            .getJSONObject("introduction")
                                                            .optJSONObject("self_resources")
                            if(null != resources) {
                                val list = resources.optJSONArray("list")
                                if(null != list && list.length() > 0) {
                                    val contents = (list.get(0) as JSONObject).optJSONArray("content")
                                    (0 until contents.length()).forEach { index ->
                                        val name: String
                                        var magnet: String
                                        val size: Long
                                        val sizeD: String
                                        val sharpness: String
                                        val caption: String
                                        val publishTime: String

                                        val item = contents.get(index) as JSONObject
                                        name = item.optString("title")
                                        magnet = item.optString("resources")
                                        magnet = magnet.substring(2, magnet.length - 2)
                                        size = item.optLong("size")
                                        if(size > 0) {
                                            sizeD = when {
                                                size > (1024 * 1024 * 1024) -> {
                                                    val ss = size.toDouble() / (1024 * 1024 * 1024)
                                                    String.format("%.4s", ss.toString()) + "G"
                                                }
                                                size > (1024 * 1024) -> {
                                                    val ss = size.toDouble() / (1024 * 1024)
                                                    String.format("%.4s", ss.toString()) + "M"
                                                }
                                                size > 1024 -> {
                                                    val ss = size.toDouble() / 1024
                                                    String.format("%.4s", ss.toString()) + "K"
                                                }
                                                else -> size.toString()
                                            }
                                        } else {
                                            sizeD = size.toString()
                                        }

                                        sharpness = item.optString("resolution")
                                        caption = item.optString("subtitle")
                                        publishTime = item.optString("date")

                                        if (
                                                !TextUtils.isEmpty(name) &&
                                                !TextUtils.isEmpty(magnet) &&
                                                !TextUtils.isEmpty(sharpness) &&
                                                !TextUtils.isEmpty(caption) &&
                                                !TextUtils.isEmpty(publishTime)
                                        ) {
                                            fileList.add(CilicatFileList(name, magnet, sizeD, sharpness, caption, publishTime))
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e("Process file list happen exception: $e")
                    }
                }
            }
        }

        return fileList
    }

    private const val ITEM_DETAIL_CONTAINER_CLASS = "BaikeCommon__summary_content"
    private const val ITEM_DETAIL_TITLE_TAG = "h1"
    private const val ITEM_DETAIL_CONTAINER_ATTRIBUTE_CLASS = "BaikeCommon__basic_info"
    private const val ITEM_DETAIL_CONTAINER_ATTRIBUTE_LEFT_CLASS = "BaikeCommon__basic_info_left"
    private const val ITEM_DETAIL_CONTAINER_ATTRIBUTE_RIGHT_CLASS = "BaikeCommon__basic_info_right"
    private const val ITEM_DETAIL_CONTAINER_DETAILS_CLASS = "BaikeCommon__summary"

    private const val ITEM_DETAIL_CONTAINER_ATTRIBUTE_TAG = "div"

    private const val HEAD_DATA_TAG = "script"
}