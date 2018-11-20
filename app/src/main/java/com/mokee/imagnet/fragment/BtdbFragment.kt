package com.mokee.imagnet.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.ybq.android.spinkit.SpinKitView
import com.mokee.imagnet.R
import com.mokee.imagnet.adapter.BtdbMeHomeAdapter
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.event.BtdbFailEvent
import com.mokee.imagnet.event.BtdbMeHomeItemEvent
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.BtdbMeItem
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.presenter.NetworkPresenter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class BtdbFragment : LazyFragment() {
    private lateinit var mHomeListView: RecyclerView
    private lateinit var mSmartRefreshLayout: SmartRefreshLayout
    private lateinit var mSpinKitView: SpinKitView

    private var homeItemList: MutableList<BtdbMeItem> = mutableListOf()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: BtdbMeHomeAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Timber.d("Btdb me fragment is attach.")
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_btdb, null)
        mHomeListView = view.findViewById(R.id.btdb_me_home_list)
        mSmartRefreshLayout = view.findViewById(R.id.btdb_me_refreshLayout)
        mSpinKitView = view.findViewById(R.id.btdb_me_loading)

        Timber.d("Btdb me fragment is prepared.")

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
        mAdapter = BtdbMeHomeAdapter(this.context!!, homeItemList)

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
            it.finishLoadMore(REFRESH_DEFAULT_TIME * 1000, false, true)
        }
    }

    private fun loadData() {
        Timber.d("Now load cilicat home url: ${MagnetConstrant.BTDB_ME_HOME_URL}.")
        NetworkPresenter.instance?.getHtmlContent(
                NetworkPresenter.NetworkItem(
                        RequestType.BTDB_ME, MagnetConstrant.BTDB_ME_HOME_URL))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onHomeItem(event: BtdbMeHomeItemEvent) {
        mSmartRefreshLayout.finishRefresh(true)
        mSmartRefreshLayout.finishLoadMore(true)
        mSpinKitView.visibility = View.GONE

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
    public fun onContentFail(event: BtdbFailEvent) {
        mSmartRefreshLayout.finishRefresh(false)
        mSmartRefreshLayout.finishLoadMore(false)
        mSpinKitView.visibility = View.GONE

        Toast.makeText(this.context, event.reason, Toast.LENGTH_SHORT).show()
    }


    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        Timber.d("Cilicat fragment is detach.")
        super.onDetach()
    }

    companion object {
        private const val REFRESH_DEFAULT_TIME = 8
    }
}