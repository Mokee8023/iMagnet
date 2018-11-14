package com.mokee.imagnet.activity.ali

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.mokee.imagnet.R
import com.mokee.imagnet.event.AliDetailItemEvent
import com.mokee.imagnet.event.CilicatDetailItemEvent
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.AliFileList
import com.mokee.imagnet.model.AliItemDetail
import com.mokee.imagnet.model.CilicatFileList
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.presenter.NetworkPresenter
import com.mokee.imagnet.utils.ClipboardUtil
import com.mokee.imagnet.utils.MagnetUtil
import kotlinx.android.synthetic.main.activity_ali_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.lang.StringBuilder

class AliDetailActivity: AppCompatActivity() {
    private lateinit var detailUrl: String

    private lateinit var fileList: List<AliFileList>
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: AliDetailActivity.AliDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_ali_detail)

        detailUrl = intent.getStringExtra(ALI_ITEM_DETAIL)
        Timber.d("Received detail url is $detailUrl")

        loadData()
        ali_detail_loading.visibility = View.VISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onDetailItem(event: AliDetailItemEvent) {
        Timber.d("Received cilicat detail item event $event")

        ali_detail_loading.visibility = View.GONE

        processDetail(event)
    }

    private fun processDetail(event: AliDetailItemEvent) {
        supportActionBar?.title = event.item.title

        val sb = StringBuilder()
        sb.append("种子Hash：").append(event.item.hash).append("\n\n")
        sb.append("文件数目：").append(event.item.fileCount).append("\n\n")
        sb.append("文件大小：").append(event.item.fileSize).append("\n\n")
        sb.append("收录时间：").append(event.item.acceptTime).append("\n\n")
        sb.append("已经下载：").append(event.item.hasDownload).append("\n\n")
        sb.append("下载速度：").append(event.item.downloadSpeed).append("\n\n")
        sb.append("最近下载：").append(event.item.recentDownload)
        ali_detail_attribute.text = sb.toString()

        // Set file list to ui
        if(event.item.fileList.isNotEmpty()) {
            fileList = event.item.fileList
            mLayoutManager = LinearLayoutManager(this)
            mAdapter = AliDetailActivity.AliDetailAdapter(this, fileList)

            ali_detail_file_list.apply {
                setHasFixedSize(true)
                this.layoutManager = mLayoutManager
                this.adapter = mAdapter
            }
        } else {
            Timber.d("File list is null or empty.")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onRequestFail(event: RequestFailEvent) {
        ali_detail_loading.visibility = View.GONE
    }

    private fun loadData() {
        NetworkPresenter.instance.getHtmlContent(
                NetworkPresenter.NetworkItem(RequestType.ALI_DETAIL, detailUrl))
    }

    companion object {
        const val ALI_ITEM_DETAIL: String = "ali_item_detail_url"
    }

    class AliDetailAdapter(context: Context, fileList: List<AliFileList>)
        : RecyclerView.Adapter<AliDetailAdapter.AliDetailHolder>() {
        private var mContext: Context = context
        private var mFileList: List<AliFileList> = fileList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AliDetailHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.ali_detail_item, parent, false)
            return AliDetailHolder(view)
        }

        override fun getItemCount(): Int {
            return mFileList.size
        }

        override fun onBindViewHolder(holder: AliDetailHolder, position: Int) {
            holder.name.text = mFileList[position].name
            holder.size.text = mFileList[position].size
        }

        class AliDetailHolder(view: View) : RecyclerView.ViewHolder(view) {
            var name: TextView = view.findViewById(R.id.ali_detail_file_list_name)
            var size: TextView = view.findViewById(R.id.ali_detail_file_list_size)
        }
    }
}