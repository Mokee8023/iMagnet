package com.mokee.imagnet.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ThunderUtil {
    fun startThunder(context: Context, thunderLink: String): Boolean {
        try{
            if(!thunderLink.startsWith("thunder://")) {
                return false
            }
            val thunderIntent = Intent(Intent.ACTION_VIEW, Uri.parse(thunderLink))
            thunderIntent.addCategory("android.intent.category.DEFAULT")
            context.startActivity(thunderIntent)
            return true
        }catch(e: Exception){
            Toast.makeText(context, "没有安装迅雷,请前往商店下载迅雷。", Toast.LENGTH_LONG).show()
            return false
        }
    }
}