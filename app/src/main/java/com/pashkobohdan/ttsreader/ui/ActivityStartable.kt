package com.pashkobohdan.ttsreader.ui

import android.content.Intent

interface ActivityStartable {

    fun startActivityForResult(requestCode: Int, intent: Intent, callback: (Intent?, Int)->Unit)
}