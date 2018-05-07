package com.pashkobohdan.ttsreader.data.usecase

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver

abstract class RunUseCase<R> : AbstractUseCase<Unit>() {

    abstract fun justDoThis(request: R)

    fun execute(request: R, subscriber: DisposableObserver<Unit>): Disposable {
        return execute(Observable.fromCallable {
            justDoThis(request)
        }, subscriber)
    }
}