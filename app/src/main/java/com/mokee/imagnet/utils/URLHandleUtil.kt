package com.mokee.imagnet.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.mokee.imagnet.webview.WebViewActivity

object URLHandleUtil {
    fun handleUrl(context: Context, url: String) {
        val innerWebview = SPUtil.getBooleanSetting(context, "setting_url_load_type", true)
        if(innerWebview) {
            URLHandleUtil.innerWebview(context, url)
        } else {
            URLHandleUtil.outWebview(context, url)
        }
    }

    fun innerWebview(context: Context, url: String) {
        val intent = Intent(context, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.WEBVIEW_LOAD_URL_KEY, url)
        context.startActivity(intent)
    }

    fun outWebview(context: Context, url: String) {
        val urlIntent = Intent("android.intent.action.VIEW")
        urlIntent.data = Uri.parse(url)
        context.startActivity(urlIntent)
    }
}