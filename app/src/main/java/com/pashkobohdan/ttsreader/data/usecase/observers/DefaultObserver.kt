package com.pashkobohdan.ttsreader.data.usecase.observers

import io.reactivex.observers.DisposableObserver

abstract class DefaultObserver <T>: DisposableObserver<T>() {
    override fun onComplete() {
        onFinally()
    }

    override fun onError(e: Throwable) {
        onFinally()
    }

    open fun onFinally() {
        //nop
    }
}