package com.mokee.imagnet.activity.setting

import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.annotation.LayoutRes
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatDelegate
import android.view.MenuInflater

abstract class AppSettingActivity : PreferenceActivity() {
    private lateinit var mDelegate: AppCompatDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        initDelegate()

        mDelegate.installViewFactory()
        mDelegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDelegate.onPostCreate(savedInstanceState)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        mDelegate.setContentView(layoutResID)
    }

    override fun onPostResume() {
        super.onPostResume()
        mDelegate.onPostResume()
    }

    fun getSupportActionBar(): ActionBar? {
        return mDelegate.supportActionBar
    }

    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        mDelegate.setTitle(title)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDelegate.onConfigurationChanged(newConfig)
    }

    override fun onStop() {
        super.onStop()
        mDelegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDelegate.onDestroy()
    }

    override fun invalidateOptionsMenu() {
        mDelegate.invalidateOptionsMenu()
    }

    private fun initDelegate() {
        mDelegate = AppCompatDelegate.create(this, null)
    }
}