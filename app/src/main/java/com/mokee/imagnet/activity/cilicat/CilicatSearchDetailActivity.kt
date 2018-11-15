package com.mokee.imagnet.activity.cilicat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mokee.imagnet.R
import com.mokee.imagnet.event.CilicatSearchDetailEvent
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.CilicatRecentItem
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.presenter.NetworkPresenter
import kotlinx.android.synthetic.main.activity_cilicat_search_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class CilicatSearchDetailActivity : AppCompatActivity() {
    private lateinit var detailUrl: String

    private lateinit var fileList: List<String>
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: CilicatSearchDetailAdapter

    private lateinit var mRecentList: List<CilicatRecentItem>
    private lateinit var mRecentLayoutManager: LinearLayoutManager
    private lateinit var mRecentAdapter: CilicatSearchDetailRecentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_cilicat_search_detail)

        detailUrl = intent.getStringExtra(CILICAT_SEARCH_ITEM_DETAIL)
        Timber.d("Received detail url is $detailUrl")

        loadData()
        cilicat_search_detail_loading.visibility = View.VISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onDetailItem(event: CilicatSearchDetailEvent) {
        Timber.d("Received cilicat search detail item event $event")

        cilicat_search_detail_loading.visibility = View.GONE

        processDetail(event)
    }

    private fun processDetail(event: CilicatSearchDetailEvent) {
        supportActionBar?.title = event.item.title

        val sb = StringBuilder()
        sb.append(event.item.hash).append("\n\n")
        sb.append(event.item.fileCount).append("\n\n")
        sb.append(event.item.fileSize).append("\n\n")
        sb.append(event.item.receivedTime).append("\n\n")
        sb.append(event.item.downloadSpeed)
        cilicat_search_detail_attribute.text = sb.toString()

        // Set file list to ui
        if (event.item.fileList.isNotEmpty()) {
            fileList = event.item.fileList
            mLayoutManager = LinearLayoutManager(this)
            mAdapter = CilicatSearchDetailAdapter(this, fileList)

            cilicat_search_detail_file_list.apply {
                setHasFixedSize(true)
                this.layoutManager = mLayoutManager
                this.adapter = mAdapter
            }
        } else {
            Timber.d("File list is null or empty.")
            cilicat_search_detail_file_list.visibility = View.GONE
        }

        // Set recent list to ui
        if (event.item.recentList.isNotEmpty()) {
            mRecentList = event.item.recentList
            mRecentLayoutManager = LinearLayoutManager(this)
            mRecentAdapter = CilicatSearchDetailRecentAdapter(this, mRecentList)

            cilicat_search_detail_recent_list.apply {
                setHasFixedSize(true)
                this.layoutManager = mRecentLayoutManager
                this.adapter = mRecentAdapter
            }
        } else {
            Timber.d("File list is null or empty.")
            cilicat_search_detail_recent_list.visibility = View.GONE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onRequestFail(event: RequestFailEvent) {
        cilicat_search_detail_loading.visibility = View.GONE
    }

    private fun loadData() {
        NetworkPresenter.instance?.getHtmlContent(
                NetworkPresenter.NetworkItem(RequestType.CILICAT_SEARCH_DETAIL, detailUrl))
    }

    companion object {
        const val CILICAT_SEARCH_ITEM_DETAIL: String = "cilicat_search_item_detail_url"
    }

    class CilicatSearchDetailAdapter(context: Context, fileList: List<String>)
        : RecyclerView.Adapter<CilicatSearchDetailAdapter.CilicatSearchDetailHolder>() {
        private var mContext: Context = context
        private var mFileList: List<String> = fileList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CilicatSearchDetailHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.cilicat_search_detail_item, parent, false)
            return CilicatSearchDetailHolder(view)
        }
        override fun getItemCount(): Int { return mFileList.size }
        override fun onBindViewHolder(holder: CilicatSearchDetailHolder, position: Int) {
            holder.file.text = mFileList[position]
        }

        class CilicatSearchDetailHolder(view: View) : RecyclerView.ViewHolder(view) {
            var file: TextView = view.findViewById(R.id.cilicat_search_detail_file_list_item_name)
        }
    }

    class CilicatSearchDetailRecentAdapter(context: Context, fileList: List<CilicatRecentItem>)
        : RecyclerView.Adapter<CilicatSearchDetailRecentAdapter.CilicatSearchDetailRecentHolder>() {
        private var mContext: Context = context
        private var mRecentList: List<CilicatRecentItem> = fileList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CilicatSearchDetailRecentHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.cilicat_search_detail_item, parent, false)
            return CilicatSearchDetailRecentHolder(view)
        }

        override fun getItemCount(): Int {
            return mRecentList.size
        }

        override fun onBindViewHolder(holder: CilicatSearchDetailRecentHolder, position: Int) {
            holder.recent.text = mRecentList[position].text

            holder.bar.setOnClickListener {
                val cilicatSearchDetail = Intent(mContext, CilicatSearchDetailActivity::class.java)
                cilicatSearchDetail.putExtra(CilicatSearchDetailActivity.CILICAT_SEARCH_ITEM_DETAIL, mRecentList[position].href)
                mContext.startActivity(cilicatSearchDetail)
            }
        }

        class CilicatSearchDetailRecentHolder(view: View) : RecyclerView.ViewHolder(view) {
            var bar: LinearLayout = view.findViewById(R.id.cilicat_search_detail_file_list_item_bar)
            var recent: TextView = view.findViewById(R.id.cilicat_search_detail_file_list_item_name)
        }
    }
}