package com.mokee.imagnet.utils

import android.content.Context
import android.text.style.ClickableSpan
import android.view.View

class ClickSpan(val context: Context, val url: String) : ClickableSpan() {
    override fun onClick(widget: View?) {
        URLHandleUtil.handleUrl(context, url)
    }
}