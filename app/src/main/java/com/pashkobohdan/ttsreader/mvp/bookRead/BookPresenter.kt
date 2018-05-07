package com.pashkobohdan.ttsreader.mvp.bookRead

import com.arellomobile.mvp.InjectViewState
import com.pashkobohdan.ttsreader.data.usecase.observers.DefaultObserver
import com.pashkobohdan.ttsreader.executors.book.GetBookByIdUseCase
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.bookRead.view.BookView
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter
import com.pashkobohdan.ttsreader.utils.TextSplitter
import javax.inject.Inject

@InjectViewState
class BookPresenter @Inject constructor() : AbstractPresenter<BookView>() {

    private val MIN_PAGE_SYMBOLS_COUNT = 800

    @Inject
    lateinit var getBookListUseCase: GetBookByIdUseCase

    private lateinit var bookDTO: BookDTO
    private var bookId: Int = 0
    private var readingSpeed: Int = 100
    private var readingPitch: Int = 100
    private var readingState: READING_STATE = READING_STATE.PAUSE

    private lateinit var text: List<List<String>>
    private lateinit var currentPage: List<String>
    private lateinit var currentSentence: String

    fun init(bookId: Int) {
        this.bookId = bookId
    }

    override fun onFirstViewAttach() {
        viewState.showProgress()
        getBookListUseCase.execute(bookId, object : DefaultObserver<BookDTO>() {

            override fun onNext(response: BookDTO) {
                bookDTO = response
                text = readPages()

                if (isBookEmpty()) {
                    viewState.hideProgress()
                    viewState.showEmptyBookError()
                    router.exit()
                } else {
                    viewState.initTtsReader()
                }
            }

            override fun onError(e: Throwable) {
                viewState.hideProgress()
                viewState.showBookExecutingError()
                router.exit()
            }
        })
    }

    fun hideHints() {
        viewState.hideHints()
    }

    fun saveBookInfo() {
        //TODO
    }

    fun ttsReaderInitSuccessfully() {
        viewState.hideProgress()
        viewState.showHints()
        viewState.initSpeedAndPitch(readingSpeed, readingPitch)
        initStartPageAndSentence()
        initPageText()
    }

    fun ttsReaderInitError() {
        viewState.showTtsReaderInitError()
    }

    private fun initPageText() {
        val (prev, current, next) = findPrevNextAndCurrentText()
        viewState.setText(prev, current, next)
    }

    private fun readPages(): List<List<String>> {
        val pages: MutableList<List<String>> = mutableListOf()
        var sentencesInPage: MutableList<String> = mutableListOf()
        var pageSymbolCount = 0
        for (sentence in TextSplitter.splitToSentences(bookDTO.text)) {
            sentencesInPage.add(sentence)
            pageSymbolCount += sentence.length
            if (pageSymbolCount > MIN_PAGE_SYMBOLS_COUNT) {
                pages.add(sentencesInPage)
                sentencesInPage = mutableListOf()
                pageSymbolCount = 0
            }
        }
        if (sentencesInPage.isNotEmpty()) {
            pages.add(sentencesInPage)
        }
        return pages
    }

    private fun isBookEmpty(): Boolean {
        if (text.isEmpty() || text[0].isEmpty() || text[0][0].isEmpty()) {
            return true
        }
        return false
    }

    private fun initStartPageAndSentence() {
        val readingProgress = Math.max(bookDTO.progress, 0)
        var sentenceNumber = 0
        currentPage = text[0]
        currentSentence = currentPage[0]
        for (page in text) {
            for (sentence in page) {
                if (sentenceNumber++.equals(readingProgress)) {
                    currentSentence = sentence
                    currentPage = page
                    break
                }
            }
        }
    }

    fun speedChanged(newSpeed: Int) {
        readingSpeed = newSpeed
    }

    fun pitchChanged(newPitch: Int) {
        readingPitch = newPitch
    }

    private fun findPrevNextAndCurrentText(): ReadingText {
        val currentSentenceIndex = currentPage.indexOf(currentSentence)
        val prevText = StringBuilder()
        for (i in 0..(currentSentenceIndex - 1)) {
            prevText.append(currentPage[i])
        }
        val nextText = StringBuilder()
        for (i in (currentSentenceIndex + 1)..(currentPage.size - 1)) {
            nextText.append(currentPage[i])
        }
        return ReadingText(prevText.toString(), currentSentence, nextText.toString())
    }

    fun back() {
        viewState.stopSpeeching()
        val sentenceInPageIndex = currentPage.indexOf(currentSentence)
        if (sentenceInPageIndex.equals(0)) {
            //need to change page
            val pageInTextIndex = text.indexOf(currentPage)
            if (pageInTextIndex.equals(0)) {
                //start of book
                pause()
                viewState.showStartOfBookAlert()
            } else {
                //go to next page
                currentPage = text[pageInTextIndex - 1]
                currentSentence = currentPage[currentPage.size - 1]
                initPageText()
                if (readingState == READING_STATE.READING) speechCurrentSentence()
            }
        } else {
            //just next sentence
            currentSentence = currentPage[sentenceInPageIndex - 1]
            initPageText()
            if (readingState == READING_STATE.READING) speechCurrentSentence()
        }
    }

    fun next() {
        viewState.stopSpeeching()
        val sentenceInPageIndex = currentPage.indexOf(currentSentence)
        if (sentenceInPageIndex.equals(currentPage.size - 1)) {
            //need to change page
            val pageInTextIndex = text.indexOf(currentPage)
            if (pageInTextIndex.equals(text.size - 1)) {
                //end of book
                pause()
                viewState.showEndOfBookAlert()
            } else {
                //go to next page
                currentPage = text[pageInTextIndex + 1]
                currentSentence = currentPage[0]
                initPageText()
                if (readingState == READING_STATE.READING) speechCurrentSentence()
            }
        } else {
            //just next sentence
            currentSentence = currentPage[sentenceInPageIndex + 1]
            initPageText()
            if (readingState == READING_STATE.READING) speechCurrentSentence()
        }
    }

    fun play() {
        readingState = READING_STATE.READING
        viewState.playMode()
        speechCurrentSentence()
    }

    fun pause() {
        readingState = READING_STATE.PAUSE
        viewState.pauseMode()
        viewState.stopSpeeching()
    }

    fun readBookFromStart() {
        currentPage = text[0]
        currentSentence = currentPage[0]
        initPageText()
    }

    private fun speechCurrentSentence() {
        viewState.speechText(currentSentence, readingSpeed, readingPitch)
    }

    fun speechDone(utteranceId: String?) {
        next()
    }

    fun speechError(utteranceId: String?, errorCode: Int) {
        //TODO
    }

    data class ReadingText(val prev: String, val current: String, val next: String)

    enum class READING_STATE {
        READING,
        PAUSE
    }
}
