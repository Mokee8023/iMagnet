package com.mokee.imagnet.utils

import android.content.Context
import android.content.Intent
import com.mokee.imagnet.webview.WebViewActivity

object DownloadUtil {
    fun download(url: String, context: Context) {
        if(url.contains("magnet")) {
            MagnetUtil.startMagnet(context, url)
        } else {
            openWeb(url, context)
        }
    }

    fun openWeb(url: String, context: Context) {
        val webviewIntent = Intent(context, WebViewActivity::class.java)
        webviewIntent.putExtra(WebViewActivity.WEBVIEW_LOAD_URL_KEY, url)
        context.startActivity(webviewIntent)
    }
}