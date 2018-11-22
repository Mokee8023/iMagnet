package com.mokee.imagnet.webview

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.mokee.imagnet.R
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.content_web_view.*
import timber.log.Timber
import java.lang.Exception
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.webkit.*
import com.mokee.imagnet.utils.URLHandleUtil
import com.tbruyelle.rxpermissions2.RxPermissions


class WebViewActivity : AppCompatActivity() {
    private var mLoadUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        initActionBar()
        initData()
        initView()
    }

    private fun initActionBar() {
        setSupportActionBar(webview_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initData() {
        try {
            mLoadUrl = if(intent.data != null) {
                intent.data.toString()
            } else {
                intent.getStringExtra(WEBVIEW_LOAD_URL_KEY)
            }
        } catch (ex: Exception) {
            Timber.e("Get load url happen exception.")
        }
        Timber.d("Received url is $mLoadUrl")
        if(TextUtils.isEmpty(mLoadUrl)) {
            Toast.makeText(this, "Url is null or empty.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView() {
        if(!TextUtils.isEmpty(mLoadUrl)) {
            initWebviewSetting()
            webview_single.loadUrl(mLoadUrl)
        }
    }

    override fun onBackPressed() { goBack() }

    private fun goBack() {
        if(webview_single.canGoBack()) {
            webview_single.goBack()
        } else {
            super.onBackPressed()
        }
    }

    // 初始化WebView Setting
    private fun initWebviewSetting() {
        webview_single.settings.javaScriptEnabled = true
        webview_single.settings.domStorageEnabled = true
        webview_single.settings.cacheMode = WebSettings.LOAD_DEFAULT

        webview_single.settings.supportZoom()

        webview_single.settings.useWideViewPort = true
        webview_single.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webview_single.settings.loadWithOverviewMode = true

        webview_single.settings.mediaPlaybackRequiresUserGesture = false

        webview_single.webChromeClient = webChromeClient
        webview_single.webViewClient = webViewClient

        webview_single.requestFocus()

        webview_single.setDownloadListener { url: String, _: String, contentDisposition: String, mimeType: String, _: Long ->
            RxPermissions(this)
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe {granted ->
                        if(granted) {
                            val request = DownloadManager.Request(Uri.parse(url))
                            val filename= URLUtil.guessFileName(url, contentDisposition, mimeType)

                            request.allowScanningByMediaScanner()
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            dm.enqueue(request)
                        } else {
                            Toast.makeText(this, "未获取权限，无法下载，请从其他通道下载.", Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private var webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            webview_load_progress.progress = 0
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            webview_load_progress.visibility = View.GONE
        }
    }

    private var webChromeClient = object: WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            supportActionBar?.title = view?.title
            Timber.d("Received title is ${view?.title}")
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            webview_load_progress.progress = newProgress
            if(newProgress >= 100 ) {
                webview_load_progress.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.webview_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                goBack()
            }
            R.id.webview_open_other -> {
                URLHandleUtil.outWebview(this, mLoadUrl)
            }
        }
        return true
    }

    companion object {
        const val WEBVIEW_LOAD_URL_KEY = "webview_load_url_key"
    }
}
