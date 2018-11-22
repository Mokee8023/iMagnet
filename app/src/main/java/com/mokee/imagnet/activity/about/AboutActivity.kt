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
        val spannableString = SpannableString(GITHUB_ADDRESS)
        val githubUrlSpan = URLSpan(GITHUB_ADDRESS)
        spannableString.setSpan(githubUrlSpan, 0, GITHUB_ADDRESS.length, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE)
        project_info.text = spannableString
        project_info.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val GITHUB_ADDRESS = "https://github.com/Mokee8023/iMagnet"
    }
}
