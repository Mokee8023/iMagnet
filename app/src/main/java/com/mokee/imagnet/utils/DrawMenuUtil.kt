package com.mokee.imagnet.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import com.mokee.imagnet.activity.about.AboutActivity
import com.mokee.imagnet.activity.setting.SettingsActivity
import java.io.File

object DrawMenuUtil {
    fun setting(context: Context) {
        context.startActivity(Intent(context, SettingsActivity::class.java))
    }

    fun share(context: Context, packageName: String) {
        Thread {
            val appInfo = context.packageManager.getPackageInfo(packageName, 0)
            val apkPath = appInfo.applicationInfo.sourceDir

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "*/*"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                shareIntent.putExtra(
                        Intent.EXTRA_STREAM,
                        FileProvider.getUriForFile(context, context.packageName + ".fileprovider", File(apkPath)))
                // 申请临时访问权限
                shareIntent.flags = (
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            } else {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(apkPath)))
            }
            context.startActivity(shareIntent)
        }.start()
    }

    fun about(context: Context) {
        val aboutIntent = Intent(context, AboutActivity::class.java)
        context.startActivity(aboutIntent)
    }
}