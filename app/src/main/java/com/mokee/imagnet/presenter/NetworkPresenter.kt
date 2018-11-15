package com.mokee.imagnet.presenter

import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.model.ResponseEvent
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class NetworkPresenter private constructor() {
    private var mRequestQueue: LinkedList<NetworkItem>
    private lateinit var mOkhttpClient: OkHttpClient

    private var isRunning: Boolean = false
    private var mCurrentUrl: String? = null
    private var mCurrentType: RequestType? = null

    init {
        initOkhttp()
        mRequestQueue = LinkedList()
    }

    private fun initOkhttp() {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        mOkhttpClient = clientBuilder.build()
    }

    fun getHtmlContent(item: NetworkItem) {
        Timber.d("received request by url: ${item.url}")
        mRequestQueue.add(item)
        next()
    }

    /**
     * Execute next http request
     */
    private fun next() {
        if(mRequestQueue.isNotEmpty()) {
            if(!isRunning) {
                getContent(mRequestQueue.remove())
            } else {
                Timber.d("Okhttp is now getting content, please waiting.")
            }
        } else {
            Timber.d("Request queue is empty, nothing to get.")
        }
    }

    /**
     * Get html content
     */
    private fun getContent(item: NetworkItem) {
        isRunning = true
        mCurrentUrl = item.url
        mCurrentType = item.type
        Timber.d("Execute get url content: ${item.url}")

        val requestBuilder = Request.Builder()
        requestBuilder.get().url(item.url)
//        val ua = getUserAgent()
//        if(ua.isNotEmpty()) {
//            requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", getUserAgent())
//        }

        val resultCall = mOkhttpClient.newCall(requestBuilder.build())

        resultCall.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                EventBus.getDefault().post(ResponseEvent(mCurrentType!!, response))
                Timber.d("Get content completed, response.headers: %s , response.body:%s.",
                        response.headers(), response.body())

                isRunning = false
                next()
            }

            override fun onFailure(call: Call, exception: IOException) {
                EventBus.getDefault().post(RequestFailEvent(call.request().url().toString(), exception))
                Timber.d("Get content fail, exception: $exception.")

                isRunning = false
                next()
            }
        })
    }

    /**
     * Get user agent
     */
    private fun getUserAgent(): String {
        var userAgent: String? = null
        try {
            userAgent = System.getProperty("http.agent")
        } catch (e: Exception) {
            Timber.e("Get http user agent happen exception: $e")
        }
        val sb = StringBuffer()
        userAgent?.let {
            (0 until userAgent.length).forEach {
                val c = userAgent[it]
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", c.toInt()))
                } else {
                    sb.append(c)
                }
            }
        }

        return sb.toString()
    }

    private object Holder {val INSTANCE = NetworkPresenter()}
    companion object {
        private const val CONNECTION_TIMEOUT = 20L
        private const val READ_TIMEOUT = 20L

         val instance: NetworkPresenter by lazy { Holder.INSTANCE }
    }

    data class NetworkItem(val type: RequestType, val url: String)
}