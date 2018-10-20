package com.mokee.imagnet.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mokee.imagnet.R
import com.mokee.imagnet.adapter.NimaHomeAdapter
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.event.NimaHomeItemEvent
import com.mokee.imagnet.event.RequestType
import com.mokee.imagnet.model.NimaItem
import com.mokee.imagnet.presenter.NetworkPresenter
import kotlinx.android.synthetic.main.fragment_nima.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class NiMaFragment : Fragment() {
    private var isPrepared: Boolean = false

    private lateinit var mHomeListView: RecyclerView

    private var homeItemList: MutableList<NimaItem> = mutableListOf()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: NimaHomeAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Timber.d("Nima fragment is attach.")
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_nima, null)
        mHomeListView = view.findViewById(R.id.nima_home_list)
        isPrepared = true
        Timber.d("Nima fragment is prepared.")
        onLazyLoad()
        return view
    }

    private fun onLazyLoad() {
        if(!isPrepared || !userVisibleHint) {
            return
        }

        initRecyclerView()

        Timber.d("Now load nima home url: ${MagnetConstrant.NIMA_HOME_URL}.")
        NetworkPresenter.instance.getHtmlContent(
                NetworkPresenter.NetworkItem(
                        RequestType.NIMA, MagnetConstrant.NIMA_HOME_URL))
    }

    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(this.context)
        mAdapter = NimaHomeAdapter(this.context!!, homeItemList)

        mHomeListView.apply {
            setHasFixedSize(true)
            this.layoutManager = mLayoutManager
            this.adapter = mAdapter
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onHomeItem(event: NimaHomeItemEvent) {
        Timber.d("Received home item event: ${event.item}")
        homeItemList.add(homeItemList.size, event.item)
        mAdapter.notifyItemInserted(homeItemList.size)
    }

    override fun onDetach() {
        EventBus.getDefault().register(this)
        Timber.d("Nima fragment is detach.")
        super.onDetach()
    }
}