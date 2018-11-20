package com.mokee.imagnet.fragment

import android.support.v4.app.Fragment

abstract class LazyFragment : Fragment() {
    private var lazyIsVisible: Boolean = false
    private var loaded: Boolean = false
    private var isPrepared: Boolean = false

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        onVisible(userVisibleHint)
    }

    private fun onVisible(visible: Boolean) {
        lazyIsVisible = visible

        if(isPrepared && !loaded && lazyIsVisible) {
            onLazyLoad()
            loaded = true
        }

        onUserVisible(lazyIsVisible)
    }

    fun isPrepared(prepared: Boolean) {
        this.isPrepared = prepared
        onVisible(lazyIsVisible)
    }

    abstract fun onLazyLoad()
    open fun onUserVisible(visible: Boolean) { }
}