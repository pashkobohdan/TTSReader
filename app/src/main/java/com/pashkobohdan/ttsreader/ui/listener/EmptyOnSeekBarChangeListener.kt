package com.pashkobohdan.ttsreader.ui.listener

import android.widget.SeekBar

abstract class EmptyOnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        //nop
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        //nop
    }
}