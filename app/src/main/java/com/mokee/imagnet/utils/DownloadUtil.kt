package com.mokee.imagnet.utils

import android.content.Context

object DownloadUtil {
    fun download(url: String, context: Context) {
        if(url.contains("magnet")) {
            MagnetUtil.startMagnet(context, url)
        } else {
            openWeb(url, context)
        }
    }

    fun openWeb(url: String, context: Context) {
        URLHandleUtil.innerWebview(context, url)
    }
}