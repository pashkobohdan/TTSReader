package com.pashkobohdan.ttsreader.data.usecase

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver

abstract class ExchangeUseCase<Req, Res> :  AbstractUseCase<Res>() {

    abstract fun getData(request: Req): Res

    fun execute(request: Req, subscriber: DisposableObserver<Res>): Disposable {
        return execute(Observable.fromCallable({
            getData(request)
        }), subscriber)
    }
}