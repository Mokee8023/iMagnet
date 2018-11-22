package com.mokee.imagnet.activity.about

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LevelListDrawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.mokee.imagnet.R
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.content_about.*


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(about_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()
    }

    private fun initView() {
        val githubString = SpannableString("Github地址：$GITHUB_ADDRESS")
        githubString.setSpan(
                URLSpan(GITHUB_ADDRESS),
                "Github地址：".length,
                githubString.length,
                SpannableString.SPAN_EXCLUSIVE_INCLUSIVE)
        project_info_github.text = githubString
        project_info_github.movementMethod = LinkMovementMethod.getInstance()

        val apkString = SpannableString("Release APK地址：$RELEASE_ADDRESS")
        apkString.setSpan(
                URLSpan(RELEASE_ADDRESS),
                "Release APK地址：".length,
                apkString.length,
                SpannableString.SPAN_EXCLUSIVE_INCLUSIVE)
        project_info_store_path.text = apkString
        project_info_store_path.movementMethod = LinkMovementMethod.getInstance()

        Thread{
            val infoHtml = """
                <p>QQ：<strong><font color="$TEXT_COLOR">$QQ</strong></p>
                <p>WeChat：<strong><font color="$TEXT_COLOR">$WECHAT</strong></p>
                <p>Email：<strong><font color="$TEXT_COLOR">$EMAIL_URL</strong></p>
                <p>WeChat Image：<br><img src="$WECHAT_IMAGE_URL"></p>""".trimIndent()
            val htmlSpan = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(infoHtml, Html.FROM_HTML_MODE_LEGACY, htmlImageGetter, null)
            } else {
                Html.fromHtml(infoHtml, htmlImageGetter, null)
            }

            runOnUiThread {
                developer_info.text = htmlSpan
                developer_info.movementMethod = LinkMovementMethod.getInstance()
            }
        }.start()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val GITHUB_ADDRESS = "https://github.com/Mokee8023/iMagnet"
        private const val RELEASE_ADDRESS = "http://45.63.60.218/owncloud/index.php/s/8Ww51EyLplabebx"
        private const val WECHAT_IMAGE_URL = "http://45.63.60.218/owncloud/index.php/apps/files_sharing/ajax/publicpreview.php?x=1920&amp;y=413&amp;a=true&amp;file=wechat.png&amp;t=BVq5F5yxlAMPnZd&amp;scalingup=0"
        private const val EMAIL_URL = "mokeevip@163.com"
        private const val QQ = "821487678"
        private const val WECHAT = "zxq821487678"
        private const val TEXT_COLOR = "#449e66"
    }

    private val htmlImageGetter = Html.ImageGetter { source ->
        val mDrawable = LevelListDrawable()
        val drawable = Glide.with(this@AboutActivity)
                .asBitmap()
                .load(source)
        runOnUiThread {
            drawable.into(object: SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                mDrawable.addLevel(1, 1, BitmapDrawable(resource))
                mDrawable.setBounds(0, 0, 200, 200)
                mDrawable.level = 1

                developer_info.invalidate()
                developer_info.text = developer_info.text
            }
        })
        }
        mDrawable
    }
}
