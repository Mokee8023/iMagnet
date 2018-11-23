package com.mokee.imagnet.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.ybq.android.spinkit.SpinKitView
import com.mokee.imagnet.MagnetPagerAdapter
import com.mokee.imagnet.MainActivity
import com.mokee.imagnet.R
import com.mokee.imagnet.adapter.NimaAdapter
import com.mokee.imagnet.event.NimaSearchEvent
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.fragment.AliFragment
import com.mokee.imagnet.fragment.BtdbFragment
import com.mokee.imagnet.fragment.CilicatFragment
import com.mokee.imagnet.fragment.NiMaFragment
import com.mokee.imagnet.fragment.search.BtdbMeSearchFragment
import com.mokee.imagnet.fragment.search.CilicatSearchFragment
import com.mokee.imagnet.fragment.search.NimaSearchFragment
import com.mokee.imagnet.model.NimaItem
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.presenter.NetworkPresenter
import com.mokee.imagnet.utils.SPUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class SearchActivity : AppCompatActivity() {
    private lateinit var mSearchText: String

    private var tabArray: MutableList<String> = mutableListOf()
    private var fragmentArray: MutableList<Fragment> = mutableListOf()

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

         mSearchText = intent.getStringExtra(SearchActivity.SEARCH_TEXT)

         initActionBar()
         initView()
         initViewPager()
    }

    private fun initActionBar() {
        supportActionBar?.title = "$mSearchText 的搜索结果如下"
    }

    private fun initView() {
        val tabs = resources.getStringArray(R.array.search_tab_text).toList()
        val selectedIndex = SPUtil.getSetSetting(this, "setting_selected_card")

        // new fragments and tab
        selectedIndex.forEach {
            when(it) {
                "0" -> {
                    tabArray.add(tabs[0])
                    fragmentArray.add(CilicatSearchFragment.newInstance(mSearchText))
                }
                "1" -> {
                    tabArray.add(tabs[1])
                    fragmentArray.add(NimaSearchFragment.newInstance(mSearchText))
                }
                "2" -> {
                    tabArray.add(tabs[2])
                    fragmentArray.add(BtdbMeSearchFragment.newInstance(mSearchText))
                }
            }
        }
    }

    /** Init view pager */
    private fun initViewPager() {
        search_pages.adapter = MagnetPagerAdapter(supportFragmentManager, tabArray, fragmentArray)
        search_pages.currentItem = FIRST_FRAGMENT_INDEX
        search_pages.offscreenPageLimit = tabArray.size

        search_tab.setupWithViewPager(search_pages)
    }

    companion object {
        const val SEARCH_TEXT = "search_text"

        const val FIRST_FRAGMENT_INDEX = 0
    }
}