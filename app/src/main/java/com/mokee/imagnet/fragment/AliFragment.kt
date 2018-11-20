package com.mokee.imagnet.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.github.ybq.android.spinkit.SpinKitView
import com.mokee.imagnet.R
import com.mokee.imagnet.adapter.AliHomeAdapter
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.event.AliFailEvent
import com.mokee.imagnet.event.AliHomeItemEvent
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.AliItem
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.presenter.NetworkPresenter
import com.mokee.imagnet.utils.SPUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class AliFragment : LazyFragment() {
    private lateinit var mHomeListView: RecyclerView
    private lateinit var mSmartRefreshLayout: SmartRefreshLayout
    private lateinit var mSpinKitView: SpinKitView

    private var homeItemList: MutableList<AliItem> = mutableListOf()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: AliHomeAdapter

    private var userVisible: Boolean = false
    private var requestContentFail: Boolean = false

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Timber.d("Ali fragment is attach.")
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ali, null)
        mHomeListView = view.findViewById(R.id.ali_home_list)
        mSmartRefreshLayout = view.findViewById(R.id.ali_refreshLayout)
        mSpinKitView = view.findViewById(R.id.ali_loading)

        Timber.d("Ali fragment is prepared.")

        super.isPrepared(true)
        return view
    }

    override fun onLazyLoad() {
        initView()
        loadData()
        mSpinKitView.visibility = View.VISIBLE
    }

    private fun initView() {
        mLayoutManager = LinearLayoutManager(this.context)
        mAdapter = AliHomeAdapter(this.context!!, homeItemList)

        mHomeListView.apply {
            setHasFixedSize(true)
            this.layoutManager = mLayoutManager
            this.adapter = mAdapter
        }

        mSmartRefreshLayout.setOnRefreshListener {
            loadData()
            it.finishRefresh(REFRESH_DEFAULT_TIME * 1000, false)
        }
        mSmartRefreshLayout.setOnLoadMoreListener {
            loadData()
            it.finishLoadMore(REFRESH_DEFAULT_TIME * 1000, false, false)
        }
    }

    private fun loadData() {
        Timber.d("Now load Ali home url: ${MagnetConstrant.ALICILI_HOME_URL}.")
        NetworkPresenter.instance?.getHtmlContent(
                NetworkPresenter.NetworkItem(
                        RequestType.ALI, MagnetConstrant.ALICILI_HOME_URL))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onHomeItem(event: AliHomeItemEvent) {
        mSmartRefreshLayout.finishRefresh(true)
        mSmartRefreshLayout.finishLoadMore(true)
        mSpinKitView.visibility = View.GONE

        requestContentFail = false

        Timber.d("Received home item event: ${event.item}")
        homeItemList.forEach {
            if(event.item == it) {
                return
            }
        }
        homeItemList.add(homeItemList.size, event.item)
        mAdapter.notifyItemInserted(homeItemList.size)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onRequestFail(event: RequestFailEvent) {
        mSmartRefreshLayout.finishRefresh(false)
        mSmartRefreshLayout.finishLoadMore(false)
        mSpinKitView.visibility = View.GONE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onContentFail(event: AliFailEvent) {
        mSmartRefreshLayout.finishRefresh(false)
        mSmartRefreshLayout.finishLoadMore(false)
        mSpinKitView.visibility = View.GONE

//        Toast.makeText(this.context, event.reason, Toast.LENGTH_SHORT).show()
        requestContentFail = true
        if(userVisible) { showDialog() }
    }

    override fun onUserVisible(visible: Boolean) {
        userVisible = visible
        if(visible && requestContentFail) {
            showDialog()
        }
    }

    private var webView: WebView? = null
    private var authDialog: AlertDialog? = null

    private fun showDialog() {
        this.context?.let {context ->
            if(SPUtil.getBooleanSetting(context, SPUtil.ALI_DIALOG_KEY)) {
                webView = WebView(context)
                webView?.let {
                    it.loadUrl(MagnetConstrant.ALICILI_HOME_URL)
                    authDialog = AlertDialog.Builder(context).setView(it).create()
                    authDialog?.show()
                }
            }
        }
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        Timber.d("Ali fragment is detach.")
        super.onDetach()
    }

    override fun onDestroy() {
        webView?.destroy()
        authDialog?.dismiss()

        webView = null
        authDialog = null

        super.onDestroy()
    }

    companion object {
        private const val REFRESH_DEFAULT_TIME = 8
    }
}