package com.mokee.imagnet.fragment

import android.app.Fragment

abstract class LazyFragment : Fragment() {
    var lazyIsVisible: Boolean = false

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

    abstract fun onLazyLoad()

    fun onVisable() { onLazyLoad() }
    fun onInVisable() { }
}