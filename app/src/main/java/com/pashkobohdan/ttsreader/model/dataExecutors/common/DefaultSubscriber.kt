package com.pashkobohdan.ttsreader.model.dataExecutors.common

open class DefaultSubscriber<T> : rx.Subscriber<T>() {

    override fun onCompleted() {
        onFinally()
    }

    override fun onError(e: Throwable) {
        onFinally()
    }

    override fun onNext(t: T) {
        //nop
    }

    open fun onFinally() {
        //nop
    }
}
