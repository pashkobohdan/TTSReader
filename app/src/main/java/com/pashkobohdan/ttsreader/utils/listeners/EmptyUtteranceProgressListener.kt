package com.pashkobohdan.ttsreader.utils.listeners

import android.speech.tts.UtteranceProgressListener

open class EmptyUtteranceProgressListener : UtteranceProgressListener() {

    override fun onDone(utteranceId: String?) {
        //nop
    }

    override fun onError(utteranceId: String?) {
        //nop
    }

    override fun onError(utteranceId: String?, errorCode: Int) {
        super.onError(utteranceId, errorCode)
    }

    override fun onStart(utteranceId: String?) {
        //nop
    }
}