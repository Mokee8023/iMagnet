package com.mokee.imagnet.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mokee.imagnet.R

class BtdbFragment : Fragment() {
    private var isPrepared: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_ali, null)
        isPrepared = true
        onLazyLoad()
        return view
    }

    private fun onLazyLoad() {
        if(!isPrepared || !userVisibleHint) {
            return
        }
    }
}