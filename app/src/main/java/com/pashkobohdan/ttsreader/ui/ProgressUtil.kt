package com.pashkobohdan.ttsreader.ui

interface ProgressUtil {

    fun showProgress()

    fun hideProgress()

    fun showProgressWithLock()

    fun hideProgressWithUnlock()
}