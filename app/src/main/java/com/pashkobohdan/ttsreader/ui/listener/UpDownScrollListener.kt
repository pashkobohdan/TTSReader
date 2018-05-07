package com.pashkobohdan.ttsreader.ui.listener

import android.support.v7.widget.RecyclerView

abstract class UpDownScrollListener : RecyclerView.OnScrollListener() {
    private val mScrollOffset = 4

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (Math.abs(dy) > mScrollOffset) {
            if (dy > 0) {
                scrollDown()
            } else {
                scrollUp()
            }
        }
    }

    abstract fun scrollDown()

    abstract fun scrollUp()
}