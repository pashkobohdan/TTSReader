package com.pashkobohdan.ttsreader.mvp.bookRead

import com.arellomobile.mvp.InjectViewState
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.bookRead.view.BookView
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter
import com.pashkobohdan.ttsreader.utils.TextSplitter
import javax.inject.Inject

@InjectViewState
class BookPresenter @Inject constructor() : AbstractPresenter<BookView>() {

    private val MIN_PAGE_SYMBOLS_COUNT = 500
    private val DIVIDE_TTS_SPEECH_RATE_BY = 120.0f

    private lateinit var bookDTO: BookDTO
    private var readingSpeed: Int = 50

    private lateinit var text: List<List<String>>
    private lateinit var currentPage: List<String>
    private lateinit var currentSentence: String

    fun init(bookDTO: BookDTO) {
        this.bookDTO = bookDTO
        this.readingSpeed = bookDTO.readingSpeed
    }

    override fun onFirstViewAttach() {
        viewState.showProgress()
        text = readPages()
        viewState.hideProgress()

        if (isBookEmpty()) {
            viewState.showEmptyBookError()
        } else {
            viewState.showHints()
            initStartPageAndSentence()
            initPageText()
        }
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

    }

    fun next() {

    }

    fun play() {
        viewState.playMode()
        speechCurrentSentence()
    }

    fun pause() {
        viewState.pauseMode()
        viewState.stopSpeeching()
    }

    private fun speechCurrentSentence() {
        viewState.speechText(currentSentence, readingSpeed / DIVIDE_TTS_SPEECH_RATE_BY)
    }

    fun speechDone(utteranceId: String?) {
        val sentenceInPageIndex = currentPage.indexOf(currentSentence)
        if(sentenceInPageIndex.equals(currentPage.size - 1)) {
            //need to change page
            val pageInTextIndex = text.indexOf(currentPage)
            if(pageInTextIndex.equals(text.size - 1)) {
                //end of book
                pause()
                //TODO show alert
            } else {
                //go to next page
                currentPage = text[pageInTextIndex + 1]
                currentSentence = currentPage[0]
                speechCurrentSentence()
            }
        } else {
            //just next sentence
            currentSentence = currentPage[sentenceInPageIndex + 1]
            speechCurrentSentence()
        }
    }

    fun speechError(utteranceId: String?, errorCode: Int) {
        //TODO
    }

    data class ReadingText(val prev: String, val current: String, val next: String)
}
