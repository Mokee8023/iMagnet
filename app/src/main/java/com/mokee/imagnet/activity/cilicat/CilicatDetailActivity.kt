package com.mokee.imagnet.activity.cilicat

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
import com.mokee.imagnet.event.CilicatDetailItemEvent
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.CilicatFileList
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.presenter.NetworkPresenter
import com.mokee.imagnet.utils.ClipboardUtil
import com.mokee.imagnet.utils.MagnetUtil
import kotlinx.android.synthetic.main.activity_cilicat_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.lang.StringBuilder

class CilicatDetailActivity: AppCompatActivity() {
    private lateinit var detailUrl: String

    private lateinit var fileList: List<CilicatFileList>
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: CilicatDetailActivity.CilicatDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_cilicat_detail)

        detailUrl = intent.getStringExtra(CILICAT_ITEM_DETAIL)
        Timber.d("Received detail url is $detailUrl")

        loadData()
        cilicat_detail_loading.visibility = View.VISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onDetailItem(event: CilicatDetailItemEvent) {
        Timber.d("Received cilicat detail item event $event")

        cilicat_detail_loading.visibility = View.GONE

        processDetail(event)
    }

    private fun processDetail(event: CilicatDetailItemEvent) {
        supportActionBar?.title = event.item.title

        val sb = StringBuilder()
        event.item.attributes.forEach {
            sb.append(it).append("\n\n")
        }
        cilicat_detail_attribute.text = sb.substring(0, sb.lastIndex - 2)

        // Set file list to ui
        if(event.item.fileList.isNotEmpty()) {
            fileList = event.item.fileList
            mLayoutManager = LinearLayoutManager(this)
            mAdapter = CilicatDetailActivity.CilicatDetailAdapter(this, fileList)

            cilicat_detail_file_list.apply {
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
        cilicat_detail_loading.visibility = View.GONE
    }

    private fun loadData() {
        NetworkPresenter.instance.getHtmlContent(
                NetworkPresenter.NetworkItem(RequestType.CILICAT_DETAIL, detailUrl))
    }

    companion object {
        const val CILICAT_ITEM_DETAIL: String = "cilicat_item_detail_url"
    }

    class CilicatDetailAdapter(context: Context, fileList: List<CilicatFileList>)
        : RecyclerView.Adapter<CilicatDetailAdapter.CilicatDetailHolder>() {
        private var mContext: Context = context
        private var mFileList: List<CilicatFileList> = fileList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CilicatDetailHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.cilicat_detail_item, parent, false)
            return CilicatDetailHolder(view)
        }

        override fun getItemCount(): Int {
            return mFileList.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: CilicatDetailHolder, position: Int) {
            holder.title.text = mFileList[position].name

            holder.size.text = mFileList[position].size
            holder.sharpness.text = mFileList[position].sharpness
            holder.publishTime.text = mFileList[position].publishTime

            holder.magnet.setOnClickListener {
                MagnetUtil.startMagnet(mContext, mFileList[position].magnet)
            }

            holder.magnet.setOnLongClickListener {
                if(!TextUtils.isEmpty(mFileList[position].magnet)) {
                    ClipboardUtil.copyToClipboard(mContext, mFileList[position].magnet)
                    Toast.makeText(mContext, "Magnet链接已复制", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }

        class CilicatDetailHolder(view: View) : RecyclerView.ViewHolder(view) {
            var title: TextView = view.findViewById(R.id.cilicat_detail_file_list_item_name)
            var magnet: ImageButton = view.findViewById(R.id.cilicat_detail_magnet)

            var size: TextView = view.findViewById(R.id.cilicat_detail_file_list_item_size)
            var sharpness: TextView = view.findViewById(R.id.cilicat_detail_file_list_item_sharpness)
            var publishTime: TextView = view.findViewById(R.id.cilicat_detail_file_list_item_publish_time)
        }
    }
}