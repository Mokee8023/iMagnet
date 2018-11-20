package com.mokee.imagnet.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.model.ResponseEvent
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NetworkPresenter private constructor(val context: Context) {
    private var mRequestQueue: LinkedList<NetworkItem>
    private lateinit var mOkhttpClient: OkHttpClient
    private lateinit var cookieJar: PersistentCookieJar

    private val executors: ExecutorService

    init {
        initOkhttp()
        mRequestQueue = LinkedList()
        executors = Executors.newFixedThreadPool(FIX_THREAD_POOL_SIZE)
    }

    private fun initOkhttp() {
        val cache = Cache(File(context.externalCacheDir.toString(),"iMagnetCache"), 10 * 1024 * 1024L)
        cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))

        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        clientBuilder.cookieJar(cookieJar)
        clientBuilder.cache(cache)

        mOkhttpClient = clientBuilder.build()
    }

    fun getHtmlContent(item: NetworkItem) {
        Timber.d("received request by url: ${item.url}")
        next(item)
    }

    private fun next(item: NetworkItem) {
        executors.execute {
            getContent(item)
        }
    }

    /**
     * Get html content
     */
    private fun getContent(item: NetworkItem) {
        val mCurrentUrl = item.url
        val mCurrentType = item.type
        Timber.d("Execute get url content: ${item.url}")

//        val cacheControl = CacheControl.Builder().maxAge(30, TimeUnit.SECONDS).build()

        val requestBuilder = Request.Builder()
        requestBuilder.get().url(item.url)/**.cacheControl(cacheControl)*/
//        val ua = getUserAgent()
//        if(ua.isNotEmpty()) {
//            requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", getUserAgent())
//        }

        val resultCall = mOkhttpClient.newCall(requestBuilder.build())

        resultCall.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                EventBus.getDefault().post(ResponseEvent(mCurrentType, response))
                Timber.d("Get %s content completed, response.headers: %s , response.body:%s.",
                        mCurrentUrl, response.headers(), response.body())
            }

            override fun onFailure(call: Call, exception: IOException) {
                EventBus.getDefault().post(RequestFailEvent(call.request().url().toString(), exception))
                Timber.d("Get $mCurrentUrl content fail, exception: $exception.")
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

    companion object {
        private const val CONNECTION_TIMEOUT = 20L
        private const val READ_TIMEOUT = 20L

        private var FIX_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1

        @SuppressLint("StaticFieldLeak")
        var instance: NetworkPresenter? = null

        fun init(context: Context): NetworkPresenter {
            if (instance == null) {
                synchronized(NetworkPresenter::class) {
                    if (instance == null) {
                        instance = NetworkPresenter(context)
                    }
                }
            }
            return instance!!
        }
    }

    data class NetworkItem(val type: RequestType, val url: String)
}