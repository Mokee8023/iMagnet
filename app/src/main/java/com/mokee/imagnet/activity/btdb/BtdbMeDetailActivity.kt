package com.mokee.imagnet.activity.btdb

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.mokee.imagnet.R
import com.mokee.imagnet.event.BtdbMeDetailItemEvent
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.BtdbMeDetailFile
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.presenter.NetworkPresenter
import com.mokee.imagnet.utils.ClipboardUtil
import com.mokee.imagnet.utils.MagnetUtil
import com.mokee.imagnet.utils.ThunderUtil
import kotlinx.android.synthetic.main.activity_btdb_me_detail.*
import kotlinx.android.synthetic.main.activity_nima_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class BtdbMeDetailActivity: AppCompatActivity() {
    private lateinit var detailUrl: String

    private lateinit var fileList: List<BtdbMeDetailFile>
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: BtdbMeDetailActivity.BtdbDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_btdb_me_detail)

        detailUrl = intent.getStringExtra(BTDB_ME_ITEM_DETAIL)
        Timber.d("Received detail url is $detailUrl")

        loadData()
        btdb_me_detail_loading.visibility = View.VISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onDetailItem(event: BtdbMeDetailItemEvent) {
        Timber.d("Received btdb me detail item event $event")

        btdb_me_detail_loading.visibility = View.GONE

        processDetail(event)
    }

    private fun processDetail(event: BtdbMeDetailItemEvent) {
        supportActionBar?.title = event.item.title

        val sb = StringBuilder()
        event.item.attributes.forEach {
            sb.append(it).append("\n\n")
        }
        btdb_me_detail_attribute.text = sb.substring(0, sb.lastIndex - 1)

        // Set file list to ui
        if(event.item.fileList.isNotEmpty()) {
            fileList = event.item.fileList
            mLayoutManager = LinearLayoutManager(this)
            mAdapter = BtdbMeDetailActivity.BtdbDetailAdapter(this, fileList)

            btdb_me_detail_file_list.apply {
                setHasFixedSize(true)
                this.layoutManager = mLayoutManager
                this.adapter = mAdapter
            }
        } else {
            Timber.d("File list is null or empty.")
        }

        if(!TextUtils.isEmpty(event.item.magnet)) {
            btdb_detail_magnet.visibility = View.VISIBLE

            btdb_detail_magnet.setOnClickListener {
                MagnetUtil.startMagnet(this, event.item.magnet)
            }
            btdb_detail_magnet.setOnLongClickListener {
                ClipboardUtil.copyToClipboard(this, event.item.magnet)
                Toast.makeText(this, "Magnet链接已复制", Toast.LENGTH_SHORT).show()
                true
            }
        } else {
            Timber.d("Magnet link is null or empty.")
        }

        if(!TextUtils.isEmpty(event.item.thunder)) {
            btdb_detail_xunlei.visibility = View.VISIBLE

            btdb_detail_xunlei.setOnClickListener {
                ThunderUtil.startThunder(this, event.item.thunder)
            }
            btdb_detail_xunlei.setOnLongClickListener {
                ClipboardUtil.copyToClipboard(this, event.item.thunder)
                Toast.makeText(this, "迅雷链接已复制", Toast.LENGTH_SHORT).show()
                true
            }
        } else {
            Timber.d("Thunder link is null or empty.")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onRequestFail(event: RequestFailEvent) {
        btdb_me_detail_loading.visibility = View.GONE
    }

    private fun loadData() {
        NetworkPresenter.instance?.getHtmlContent(
                NetworkPresenter.NetworkItem(RequestType.BTDB_ME_DETAIL, detailUrl))
    }

    companion object {
        const val BTDB_ME_ITEM_DETAIL: String = "btdb_me_item_detail_url"
    }

    class BtdbDetailAdapter(context: Context, fileList: List<BtdbMeDetailFile>)
        : RecyclerView.Adapter<BtdbDetailAdapter.BtdbDetailHolder>() {
        private var mContext: Context = context
        private var mFileList: List<BtdbMeDetailFile> = fileList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtdbDetailHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.btdb_me_detail_item, parent, false)
            return BtdbDetailHolder(view)
        }

        override fun getItemCount(): Int { return mFileList.size }

        override fun onBindViewHolder(holder: BtdbDetailHolder, position: Int) {
            holder.name.text = "${mFileList[position].name}     ${mFileList[position].size}"
//            holder.size.text = mFileList[position].size
        }

        class BtdbDetailHolder(view: View) : RecyclerView.ViewHolder(view) {
            var name: TextView = view.findViewById(R.id.btdb_me_detail_file_list_item_name)
//            var size: TextView = view.findViewById(R.id.btdb_me_detail_file_list_item_size)
        }
    }
}