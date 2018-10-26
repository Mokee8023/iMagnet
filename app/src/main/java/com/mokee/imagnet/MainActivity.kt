package com.mokee.imagnet

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.event.ResponseEvent
import com.mokee.imagnet.fragment.*
import com.mokee.imagnet.presenter.MessagePresenter
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

    /** Init data */
    private fun initData() {
        tabArray = resources.getStringArray(R.array.main_tab_text).toList()
        // new fragments
        fragmentArray.add(NiMaFragment())
//        fragmentArray.add(CilicatFragment())
//        fragmentArray.add(AliFragment())
//        fragmentArray.add(BtdbFragment())
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

        magnet_source.setupWithViewPager(main_magnet_pages)
    }

    private fun initPresenter() {
        mMessagePresenter = MessagePresenter()
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_camera -> {
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

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