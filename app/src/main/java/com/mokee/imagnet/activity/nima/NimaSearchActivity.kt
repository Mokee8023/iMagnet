package com.mokee.imagnet.activity.nima

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.ybq.android.spinkit.SpinKitView
import com.mokee.imagnet.R
import com.mokee.imagnet.adapter.NimaAdapter
import com.mokee.imagnet.event.NimaSearchEvent
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.NimaItem
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.presenter.NetworkPresenter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import kotlin.math.sign

class NimaSearchActivity : AppCompatActivity() {
    private lateinit var mSearchUrl: String
    private lateinit var mSearchText: String

    private lateinit var mSearchListView: RecyclerView
    private lateinit var mSmartRefreshLayout: SmartRefreshLayout
    private lateinit var mSpinKitView: SpinKitView

    private var mSearchItemList: MutableList<NimaItem> = mutableListOf()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: NimaAdapter

    private var mCurrentSearchPage: Int = 0

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_nima_search)

        initView()
    }

    private fun initView() {
        mSearchListView = findViewById(R.id.nima_search_list)
        mSmartRefreshLayout = findViewById(R.id.nima_search_refreshLayout)
        mSpinKitView = findViewById(R.id.nima_search_loading)

        mSearchUrl = intent.getStringExtra(SEARCH_EXTRA_URL)
        mSearchText = intent.getStringExtra(SEARCH_TEXT)
        mCurrentSearchPage = 1

        supportActionBar?.title = mSearchText

        mLayoutManager = LinearLayoutManager(this)
        mAdapter = NimaAdapter(this, mSearchItemList)

        mSearchListView.apply {
            setHasFixedSize(true)
            this.layoutManager = mLayoutManager
            this.adapter = mAdapter
        }

        mSmartRefreshLayout.setOnRefreshListener {
            loadData()
            it.finishRefresh(REFRESH_DEFAULT_TIME * 1000, false)
        }
        mSmartRefreshLayout.setOnLoadMoreListener {
            loadMoreData()
            it.finishLoadMore(REFRESH_DEFAULT_TIME * 1000, false, true)
        }
    }

    private fun loadData() {
        Timber.d("Now load nima search url: $mSearchUrl.")
        NetworkPresenter.instance.getHtmlContent(
                NetworkPresenter.NetworkItem(RequestType.NIMA_SEARCH, mSearchUrl))
    }

    private fun loadMoreData() {
        mCurrentSearchPage++
        val moreSearchUrl = mSearchUrl.replaceRange(
                mSearchUrl.length - 1, mSearchUrl.length, mCurrentSearchPage.toString())
        Timber.d("Now load nima search url: $moreSearchUrl.")

        NetworkPresenter.instance.getHtmlContent(
                NetworkPresenter.NetworkItem(RequestType.NIMA_SEARCH, moreSearchUrl))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onSearchEvent(event: NimaSearchEvent) {
        mSmartRefreshLayout.finishRefresh(true)
        mSmartRefreshLayout.finishLoadMore(true)
        mSpinKitView.visibility = View.GONE

        Timber.d("Received search item event: ${event.item}")
        mSearchItemList.forEach {
            if(event.item == it) {
                return
            }
        }
        mSearchItemList.add(mSearchItemList.size, event.item)
        mAdapter.notifyItemInserted(mSearchItemList.size)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onRequestFail(event: RequestFailEvent) {
        mSmartRefreshLayout.finishRefresh(false)
        mSmartRefreshLayout.finishLoadMore(false)
        mSpinKitView.visibility = View.GONE

        if(mCurrentSearchPage > 0) {
            mCurrentSearchPage--
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    companion object {
        const val SEARCH_EXTRA_URL = "nima_search_url"
        const val SEARCH_TEXT = "nima_search_text"

        private const val REFRESH_DEFAULT_TIME = 30
    }
}