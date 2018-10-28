package com.mokee.imagnet.activity.nima

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewSwitcher
import com.mokee.imagnet.R
import com.mokee.imagnet.event.NimaDetailItemEvent
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.model.NimaFuli
import com.mokee.imagnet.model.NimaItemDetail
import com.mokee.imagnet.presenter.NetworkPresenter
import com.mokee.imagnet.utils.ClipboardUtil
import com.mokee.imagnet.utils.MagnetUtil
import com.mokee.imagnet.utils.ThunderUtil
import kotlinx.android.synthetic.main.activity_nima_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.util.*

class NimaDetailsActivity : AppCompatActivity(){
    private lateinit var detailUrl: String

    private lateinit var fileList: List<String>
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: NimaDetailAdapter

    private lateinit var fuliList: List<NimaFuli>

    private var mNimaItemDetail: NimaItemDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_nima_detail)

        detailUrl = intent.getStringExtra(NIMA_ITEM_DETAIL)
        Timber.d("Received detail url is $detailUrl")

        loadData()
        nima_detail_loading.visibility = View.VISIBLE
        initView()
    }

    private fun loadData() {
        NetworkPresenter.instance.getHtmlContent(
                NetworkPresenter.NetworkItem(RequestType.NIMA_DETAIL, detailUrl))
    }

    private fun initView() {
        nima_detail_attribute_bar.setOnLongClickListener {
            if(nima_detail_fuli_list.visibility == View.INVISIBLE) {
                nima_detail_fuli_list.visibility = View.VISIBLE
                runTimer()
            } else {
                nima_detail_fuli_list.visibility = View.INVISIBLE
                stopTimer()
            }
            true
        }

        nima_detail_fuli_list.setFactory(mTextFactory)

        nima_detail_refreshLayout.setOnRefreshListener {
            loadData()
            it.finishRefresh(REFRESH_DEFAULT_TIME * 1000, false)
        }

        nima_detail_magnet.setOnClickListener {
            if(!TextUtils.isEmpty(mNimaItemDetail?.magnet)) {
                MagnetUtil.startMagnet(this@NimaDetailsActivity, mNimaItemDetail!!.magnet)
            }
        }
        nima_detail_magnet.setOnLongClickListener {
            if(!TextUtils.isEmpty(mNimaItemDetail?.magnet)) {
                ClipboardUtil.copyToClipboard(this@NimaDetailsActivity, mNimaItemDetail!!.magnet)
                Toast.makeText(this, "Magnet链接已复制", Toast.LENGTH_SHORT).show()
            }
            true
        }

        nima_detail_xunlei.setOnClickListener {
            if(!TextUtils.isEmpty(mNimaItemDetail?.thunder)) {
                ThunderUtil.startThunder(this@NimaDetailsActivity, mNimaItemDetail!!.thunder)
            }
        }
        nima_detail_xunlei.setOnLongClickListener {
            if(!TextUtils.isEmpty(mNimaItemDetail?.thunder)) {
                ClipboardUtil.copyToClipboard(this@NimaDetailsActivity, mNimaItemDetail!!.magnet)
                Toast.makeText(this, "迅雷链接已复制", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private var timer: Timer? = null
    private var timerCount = 0
    private var timerTask: TimerTask? = null
    /**
     * Run timer for text switcher
     */
    private fun runTimer() {
        timer = Timer()
        timerTask = object: TimerTask() {
            override fun run() {
                timerCount++
                if(fuliList.isNotEmpty()) {
                    runOnUiThread {
                        val showSpan = SpannableString(fuliList[timerCount % fuliList.size].text)
                        showSpan.setSpan(URLSpan(fuliList[timerCount % fuliList.size].href),
                                0, showSpan.length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
                        nima_detail_fuli_list.setText(showSpan)
                    }
                }
            }
        }
        timer?.schedule(timerTask, TIMER_PERIOD * 1000L, TIMER_PERIOD * 1000L)
    }

    /**
     * Stop timer for text switcher
     */
    private fun stopTimer() {
        timer?.cancel()
        timerTask?.cancel()
        timer = null
        timerTask = null
        timerCount = 0
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onDetailItem(event: NimaDetailItemEvent) {
        Timber.d("Received nima detail item event $event")
        mNimaItemDetail = event.item

        nima_detail_refreshLayout.finishRefresh(true)
        nima_detail_loading.visibility = View.GONE

        if(!TextUtils.isEmpty(event.item.magnet)) {
            nima_detail_magnet.visibility = View.VISIBLE
        }

        if(!TextUtils.isEmpty(event.item.thunder)) {
            nima_detail_xunlei.visibility = View.VISIBLE
        }

        processDetail(event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onRequestFail(event: RequestFailEvent) {
        nima_detail_loading.visibility = View.GONE
        nima_detail_refreshLayout.finishRefresh(false)
    }

    /**
     * Process detail event and update ui
     */
    private fun processDetail(event: NimaDetailItemEvent) {
        // Set action bar
        if(!TextUtils.isEmpty(event.item.title))  {
            val title = event.item.title

            supportActionBar?.title = title
            nima_detail_title.text = title
        }

        // Set attribute
        val sb = StringBuilder()
        sb.append("关键词：")
        event.item.keyWords.forEach {
            sb.append(it).append(" ")
        }

        sb.append("\n\n").append("文件大小：").append(event.item.fileSize)
        sb.append("\n\n").append("最后活跃：").append(event.item.lastActive)
        sb.append("\n\n").append("活跃热度：").append(event.item.activeHot)

        nima_detail_attribute.text = sb.toString()

        // Set file list to ui
        if(event.item.fileList.isNotEmpty()) {
            fileList = event.item.fileList
            mLayoutManager = LinearLayoutManager(this)
            mAdapter = NimaDetailAdapter(this, fileList)

            nima_detail_file_list.apply {
                setHasFixedSize(true)
                this.layoutManager = mLayoutManager
                this.adapter = mAdapter
            }
        } else {
            Timber.d("File list is null or empty.")
        }

        // Set fuli to UI
        if (event.item.fuliList.isNotEmpty()) {
            fuliList = event.item.fuliList
            val showSpan = SpannableString(fuliList[0].text)
            showSpan.setSpan(URLSpan(fuliList[0].href),
                    0, showSpan.length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
            nima_detail_fuli_list.setCurrentText(showSpan)
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    companion object {
        const val NIMA_ITEM_DETAIL = "detail_url"
        private const val TIMER_PERIOD = 5
        private const val REFRESH_DEFAULT_TIME = 8
    }

    class NimaDetailAdapter(context: Context, fileList: List<String>)
                                : RecyclerView.Adapter<NimaDetailAdapter.NimaDetailHolder>() {
        private var mContext: Context = context
        private var mFileList: List<String> = fileList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NimaDetailHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.nima_detail_item, parent, false)
            return NimaDetailAdapter.NimaDetailHolder(view)
        }

        override fun getItemCount(): Int {
            return mFileList.size
        }

        override fun onBindViewHolder(holder: NimaDetailHolder, position: Int) {
            holder.fileListView.text = (position + 1).toString() + "、" + mFileList[position]
        }

        class NimaDetailHolder(view: View) : RecyclerView.ViewHolder(view) {
            var fileListView: TextView = view.findViewById(R.id.nima_detail_file_list_item)
        }
    }

    /**
     * The {@link android.widget.ViewSwitcher.ViewFactory} used to create {@link android.widget.TextView}s that the
     * {@link android.widget.TextSwitcher} will switch between.
     */

    private val mTextFactory = ViewSwitcher.ViewFactory {
        val textView = TextView(applicationContext)
        textView.gravity = Gravity.CENTER or Gravity.LEFT
        textView.maxLines = 2
        textView.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        textView.setTextAppearance(applicationContext, android.R.style.TextAppearance_DeviceDefault)
        textView.movementMethod = LinkMovementMethod.getInstance()
         textView
    }
}