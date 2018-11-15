package com.mokee.imagnet.fragment.search

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.ybq.android.spinkit.SpinKitView
import com.mokee.imagnet.R
import com.mokee.imagnet.adapter.NimaAdapter
import com.mokee.imagnet.constrant.MagnetConstrant
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
import java.net.URLEncoder

class NimaSearchFragment : Fragment() {

    private var mSearchUrl: String? = null
    private var mSearchText: String? = null

    private lateinit var mSearchListView: RecyclerView
    private lateinit var mSmartRefreshLayout: SmartRefreshLayout
    private lateinit var mSpinKitView: SpinKitView

    private var mSearchItemList: MutableList<NimaItem> = mutableListOf()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: NimaAdapter

    private var mCurrentSearchPage: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSearchText = arguments?.getString(SEARCH_EXTRA_TEXT)
        mSearchText?.let {
            mSearchUrl = MagnetConstrant.NIMA_SEARCH_URL + URLEncoder.encode(it) + "-hot-desc-1"
            NetworkPresenter.instance?.getHtmlContent(
                    NetworkPresenter.NetworkItem(RequestType.NIMA_SEARCH, mSearchUrl!!))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_nima_search, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        mSearchListView = view.findViewById(R.id.search_nima_list)
        mSmartRefreshLayout = view.findViewById(R.id.search_nima_refreshLayout)
        mSpinKitView = view.findViewById(R.id.search_nima_loading)

        mCurrentSearchPage = 1

        mLayoutManager = LinearLayoutManager(this.context)
        mAdapter = NimaAdapter(this.context!!, mSearchItemList)

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
        mSearchUrl?.let {
            NetworkPresenter.instance?.getHtmlContent(
                    NetworkPresenter.NetworkItem(RequestType.NIMA_SEARCH, it))
        }?: Timber.e("Search url is null.")
    }

    private fun loadMoreData() {
        mSearchUrl?.let {
            mCurrentSearchPage++
            val moreSearchUrl = it.replaceRange(
                    it.length - 1, it.length, mCurrentSearchPage.toString())
            Timber.d("Now load nima search url: $moreSearchUrl.")

            NetworkPresenter.instance?.getHtmlContent(
                    NetworkPresenter.NetworkItem(RequestType.NIMA_SEARCH, moreSearchUrl))
        }?: Timber.e("Search url is null.")
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

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }

    companion object {
        const val SEARCH_EXTRA_TEXT = "nima_search_text"

        private const val REFRESH_DEFAULT_TIME = 30

        @JvmStatic
        fun newInstance(searchUrl: String) =
                NimaSearchFragment().apply {
                    arguments = Bundle().apply {
                        putString(SEARCH_EXTRA_TEXT, searchUrl)
                    }
                }
    }
}
