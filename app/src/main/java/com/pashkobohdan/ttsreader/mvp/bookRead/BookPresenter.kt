package com.pashkobohdan.ttsreader.mvp.bookRead

import com.arellomobile.mvp.InjectViewState
import com.pashkobohdan.ttsreader.mvp.bookRead.view.BookView
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter
import com.pashkobohdan.ttsreader.service.TtsListener
import javax.inject.Inject

@InjectViewState
class BookPresenter @Inject constructor() : AbstractPresenter<BookView>() {

    private var bookId: Long = 0
    private var readingSpeed: Int = 100
    private var readingPitch: Int = 100

    private var service: TtsListener?=null

    fun init(bookId: Long) {
        this.bookId = bookId
    }

    override fun onFirstViewAttach() {
        viewState.showProgress()
        viewState.initTtsReader()
    }

    fun serviceCreated(service: TtsListener) {
        this.service = service
        viewState.showProgress()
        service.loadBook(bookId, {
            service.init({
                ttsReaderInitSuccessfully()
                service.setTextReadingListener({
                    val (prev, current, next) = it
                    viewState.setText(prev, current, next)
                }, {
                    viewState.playMode()
                }, {
                    viewState.pauseMode()
                }, {
//                    viewState. TODO !
                }, {
                    viewState.showEndOfBookAlert()
                })
            }, {
                ttsReaderInitError()
            })
        }, {
            viewState.hideProgress()
            viewState.showBookExecutingError()
            router.exit()
        }, {
            viewState.hideProgress()
            viewState.showEmptyBookError()
            router.exit()
        }, { speedRate, pitchRate ->
            viewState.initSpeedAndPitch(speedRate, pitchRate)
        })
    }

    fun closeNotificationIfPause() {
        service?.stopIfPause()
    }

    fun hideHints() {
        viewState.hideHints()
    }

    fun saveBookInfo() {
        service?.saveCurrentBookInfo()
    }

    fun ttsReaderInitSuccessfully() {
        viewState.hideProgress()
        viewState.showHints()
    }

    fun ttsReaderInitError() {
        viewState.showTtsReaderInitError()
    }

    fun speedChanged(newSpeed: Int) {
        readingSpeed = newSpeed
        service?.changeBookInfo(readingSpeed, readingPitch)
    }

    fun pitchChanged(newPitch: Int) {
        readingPitch = newPitch
        service?.changeBookInfo(readingSpeed, readingPitch)
    }

    fun back() {
        service?.prevSentence()
    }

    fun next() {
        service?.nextSentence()
    }

    fun play() {
        service?.resume()
    }

    fun pause() {
        service?.pause()
    }

    fun readBookFromStart() { // TODO !!!
//        currentPage = text[0]
//        currentSentence = currentPage[0]
//        initPageText()
    }

    enum class READING_STATE {
        READING,
        PAUSE
    }
}
