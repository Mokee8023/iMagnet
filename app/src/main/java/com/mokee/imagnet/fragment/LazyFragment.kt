package com.mokee.imagnet.fragment

import android.support.v4.app.Fragment

abstract class LazyFragment : Fragment() {
    private var lazyIsVisible: Boolean = false
    private var loaded: Boolean = false
    private var isPrepared: Boolean = false

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if(userVisibleHint) {
            lazyIsVisible = true
            onVisable()
        } else {
            lazyIsVisible = false
            onInVisable()
        }
    }

    fun isPrepared(prepared: Boolean) {
        this.isPrepared = prepared
        onVisable()
    }

    abstract fun onLazyLoad()

    private fun onVisable() {
        if(isPrepared && !loaded && lazyIsVisible) {
            onLazyLoad()
            loaded = true
        }
    }
    private fun onInVisable() { }
}