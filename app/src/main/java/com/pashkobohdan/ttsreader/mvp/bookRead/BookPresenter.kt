package com.pashkobohdan.ttsreader.mvp.bookRead

import com.arellomobile.mvp.InjectViewState
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.bookRead.view.BookView
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter
import com.pashkobohdan.ttsreader.service.TtsListener
import com.pashkobohdan.ttsreader.utils.TextSplitter
import java.util.*
import javax.inject.Inject

@InjectViewState
class BookPresenter @Inject constructor() : AbstractPresenter<BookView>() {

    private val READING_MODE = 1
    private val PAGES_MODE = 2

    private var currentMode = READING_MODE

    private var bookId: Long = 0
    lateinit var bookDto: BookDTO
    private var readingSpeed: Int = 100
    private var readingPitch: Int = 100

    private var service: TtsListener? = null


    lateinit var bookPageInfo: TextSplitter.Companion.BookPagesInfo

    fun init(bookId: Long) {
        this.bookId = bookId
    }

    override fun onFirstViewAttach() {
        viewState.showProgress()
        viewState.initTtsReader()
        viewState.endPagesMode()
    }

    fun serviceCreated(service: TtsListener) {
        this.service = service
        viewState.showProgress()
        service.loadBook(bookId, {
            this.bookDto = it
            viewState.setBookTitle(it.name)
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
                    //viewState. TODO !
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

    fun readBookFromStart() {
        service?.moveToStartOfBook()
    }

    fun startBookPageMode() {
        currentMode = PAGES_MODE
        viewState.startPagesMode()
        this.bookPageInfo = TextSplitter.readPagesText(bookDto)
        viewState.setPagesText(bookPageInfo)
    }

    fun endBookPageMode() {
        currentMode = READING_MODE
        viewState.endPagesMode()
    }

    fun pageSelected(page: Int) {
        goToPage(page)
        endBookPageMode()
        service?.currentPageSelected(getPageNumberInBounds(page))
    }

    fun goToPage(page: Int) {
        viewState.selectPage(getPageNumberInBounds(page))
    }

    private fun getPageNumberInBounds(page: Int): Int {
        if (page < 1) {
            return 0
        } else if (page > bookPageInfo.text.size) {
            return bookPageInfo.text.size - 1
        } else {
            return page - 1
        }
    }

    fun changeLanguage() {
        val languages = service?.getAvailableLanguages()
        if(languages == null) {
            viewState.shoNoAvailableLanguagesError()
        }else {
            viewState.showSelectLanguageDialog(languages)
        }
    }

    fun languageChanged(locale: Locale) {
        service?.changeReadingLanguage(locale)
    }

    override fun backNavigation() {
        if (currentMode == PAGES_MODE) {
            endBookPageMode()
        } else {
            super.backNavigation()
        }
    }

    enum class READING_STATE {
        READING,
        PAUSE
    }
}
