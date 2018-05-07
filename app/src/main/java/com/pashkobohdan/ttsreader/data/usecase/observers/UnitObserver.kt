package com.pashkobohdan.ttsreader.data.usecase.observers

abstract class UnitObserver : DefaultObserver<Unit>() {

    override fun onNext(t: Unit) = onNext()

    abstract fun onNext()
}