package com.mokee.imagnet.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View

class SoftKeyBoardListener(activity: Activity, softKeyBoardChangeListener: OnSoftKeyBoardChangeListener) {
    private val rootView: View = activity.window.decorView
    private var rootViewVisibleHeight: Int = 0
    private var mSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener = softKeyBoardChangeListener

    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)

            val visibleHeight = rect.height()
            when {
                (rootViewVisibleHeight == 0) -> { rootViewVisibleHeight = visibleHeight }
                (rootViewVisibleHeight == visibleHeight) -> {  }
                (rootViewVisibleHeight - visibleHeight > 200) -> {
                    mSoftKeyBoardChangeListener.keyBoardShow(rootViewVisibleHeight - visibleHeight)
                    rootViewVisibleHeight = visibleHeight
                }
                (visibleHeight - rootViewVisibleHeight > 200) -> {
                    mSoftKeyBoardChangeListener.keyBoardHide(visibleHeight - rootViewVisibleHeight)
                    rootViewVisibleHeight = visibleHeight
                }
            }
        }
    }

    public interface OnSoftKeyBoardChangeListener {
        fun keyBoardShow(height: Int)
        fun keyBoardHide(height: Int)
    }
}