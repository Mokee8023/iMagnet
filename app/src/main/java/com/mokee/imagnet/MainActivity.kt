package com.mokee.imagnet

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.mokee.imagnet.activity.SearchActivity
import com.mokee.imagnet.activity.setting.SettingsActivity
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.fragment.AliFragment
import com.mokee.imagnet.fragment.BtdbFragment
import com.mokee.imagnet.fragment.CilicatFragment
import com.mokee.imagnet.fragment.NiMaFragment
import com.mokee.imagnet.model.ResponseEvent
import com.mokee.imagnet.presenter.MessagePresenter
import com.mokee.imagnet.presenter.NetworkPresenter
import com.mokee.imagnet.utils.DrawMenuUtil
import com.mokee.imagnet.utils.SoftKeyBoardListener
import com.mokee.imagnet.utils.SoftKeyBoardListener.OnSoftKeyBoardChangeListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var tabArray: List<String>
    private var fragmentArray: MutableList<Fragment> = mutableListOf()

    // Presenter
    private lateinit var mMessagePresenter: MessagePresenter

    private lateinit var mSearchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        EventBus.getDefault().register(this)

        initData()
        initBar()
        initView()
        initPresenter()
    }

    override fun onResume() {
        super.onResume()
        SoftKeyBoardListener(this, object: OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int) { }
            override fun keyBoardHide(height: Int) {
                mSearchView.clearFocus()
                toolbar.isFocusable = true
                toolbar.isFocusableInTouchMode = true
                toolbar.requestFocus()
            }
        })
    }

    /** Init data */
    private fun initData() {
        tabArray = resources.getStringArray(R.array.main_tab_text).toList()
        // new fragments
        fragmentArray.add(NiMaFragment())
        fragmentArray.add(CilicatFragment())
        fragmentArray.add(AliFragment())
        fragmentArray.add(BtdbFragment())
    }

    /** Init bar */
    private fun initBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initTab()
    }

    /** Init tab */
    private fun initTab() {
        tabArray.forEach {
            magnet_source.addTab(magnet_source.newTab().setText(it))
        }
    }

    private fun initView() {
        initViewPager()

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    /** Init view pager */
    private fun initViewPager() {
        main_magnet_pages.adapter = MagnetPagerAdapter(supportFragmentManager, tabArray, fragmentArray)
        main_magnet_pages.currentItem = FIRST_FRAGMENT_INDEX
        main_magnet_pages.offscreenPageLimit = tabArray.size

        magnet_source.setupWithViewPager(main_magnet_pages)
    }

    private fun initPresenter() {
        mMessagePresenter = MessagePresenter()
        NetworkPresenter.init(this)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public fun onResponse(event: ResponseEvent) {
        mMessagePresenter.processResponse(event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onRequestFail(event: RequestFailEvent) {
        mMessagePresenter.processRequestFail(this.applicationContext, event)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val menuItem = menu.findItem(R.id.action_search)
        mSearchView = menuItem.actionView as SearchView

        initSearchView()
        return true
    }

    private fun initSearchView() {
        mSearchView.maxWidth = Integer.MAX_VALUE
        mSearchView.isSubmitButtonEnabled = true
        mSearchView.queryHint = "Search..."
        mSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean { return true }
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if(!TextUtils.isEmpty(it)) {
                        mSearchView.isIconified = true
                        mSearchView.isIconified = true

                        val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                        searchIntent.putExtra(SearchActivity.SEARCH_TEXT, it)
                        startActivity(searchIntent)
                    } else {
                        Toast.makeText(this@MainActivity, "查询的内容为空.", Toast.LENGTH_SHORT).show()
                        Timber.d("Query Text is empty.")
                    }
                }
                return true
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mSearchView?.let {
                if (!mSearchView.isIconified) {
                    mSearchView.isIconified = true
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_setting -> {
                DrawMenuUtil.setting(this)
            }
            R.id.nav_share -> {
                DrawMenuUtil.share(this, packageName)
            }
            R.id.nav_about -> {
                DrawMenuUtil.about(this)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    companion object {
        const val FIRST_FRAGMENT_INDEX = 0
    }
}