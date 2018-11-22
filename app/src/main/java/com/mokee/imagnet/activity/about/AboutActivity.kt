package com.mokee.imagnet.activity.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MenuItem
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
    }
}
