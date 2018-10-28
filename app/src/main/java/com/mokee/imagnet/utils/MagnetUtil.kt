package com.mokee.imagnet.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import timber.log.Timber

object MagnetUtil {
    fun startMagnet(context: Context, magnetLink: String): Boolean {
        Timber.d("Received magnet link is $magnetLink")
        try{
            if(!magnetLink.startsWith("magnet:?")) {
                return false
            }
            val magnetIntent = Intent(Intent.ACTION_VIEW, Uri.parse(magnetLink))
            magnetIntent.addCategory("android.intent.category.DEFAULT")
            context.startActivity(magnetIntent)
            return true
        }catch(e: Exception){
            Toast.makeText(context, "没有安装磁力链接的软件", Toast.LENGTH_LONG).show()
            return false
        }
    }
}