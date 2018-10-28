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
import com.mokee.imagnet.adapter.CilicatHomeAdapter
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.model.CilicatItem
import com.mokee.imagnet.presenter.NetworkPresenter
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class CilicatFragment : Fragment() {
    private var isPrepared: Boolean = false

    private lateinit var mHomeListView: RecyclerView

    private var homeItemList: MutableList<CilicatItem> = mutableListOf()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: CilicatHomeAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Timber.d("Cilicat fragment is attach.")
//        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cilicat, null)
        mHomeListView = view.findViewById(R.id.cilicat_home_list)
        isPrepared = true
        onLazyLoad()
        return view
    }

    private fun onLazyLoad() {
        if(!isPrepared || !userVisibleHint) {
            return
        }
        initRecyclerView()

        Timber.d("Now load cilicat home url: ${MagnetConstrant.CILICAT_HOME_URL}.")
        NetworkPresenter.instance.getHtmlContent(
                NetworkPresenter.NetworkItem(
                        RequestType.CILICAT, MagnetConstrant.CILICAT_HOME_URL))
    }

    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(this.context)
        mAdapter = CilicatHomeAdapter(this.context!!, homeItemList)

        mHomeListView.apply {
            setHasFixedSize(true)
            this.layoutManager = mLayoutManager
            this.adapter = mAdapter
        }
    }
    override fun onDetach() {
        EventBus.getDefault().register(this)
        Timber.d("Cilicat fragment is detach.")
        super.onDetach()
    }
}