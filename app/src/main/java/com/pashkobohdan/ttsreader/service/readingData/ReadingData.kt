package com.pashkobohdan.ttsreader.service.readingData

import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.utils.Constants
import com.pashkobohdan.ttsreader.utils.Patterns
import java.util.regex.Pattern

data class ReadingText(val pages: List<ReadingPage>) {
    val text: String
        get() {
            val sb = StringBuilder()
            pages.forEach { sb.append(it.text) }
            return sb.toString()
        }


    override fun toString() = text

    override fun equals(other: Any?): Boolean {
        return this.javaClass === other?.javaClass && this === other
    }
}

data class ReadingPage(val sentences: List<ReadingSentence>) {
    val text: String
        get() {
            val sb = StringBuilder()
            sentences.forEach { sb.append(it.text) }
            return sb.toString()
        }

    override fun toString() = text

    override fun equals(other: Any?): Boolean {
        return this.javaClass === other?.javaClass && this === other
    }
}

data class ReadingSentence(val text: String) {
    val length: Int
        get() = text.length


    override fun toString() = text

    override fun equals(other: Any?): Boolean {
        return this.javaClass === other?.javaClass && this === other
    }
}

object ReadingData {

    val MIN_PAGE_SYMBOLS_COUNT = 800

    fun readBook(bookDTO: BookDTO): ReadingText {
        val pages: MutableList<ReadingPage> = mutableListOf()
        var sentencesInPage: MutableList<ReadingSentence> = mutableListOf()
        var pageSymbolCount = 0
        for (sentence in splitToSentences(bookDTO.text)) {
            sentencesInPage.add(sentence)
            pageSymbolCount += sentence.length
            if (pageSymbolCount > MIN_PAGE_SYMBOLS_COUNT) {
                pages.add(ReadingPage(sentencesInPage))
                sentencesInPage = mutableListOf()
                pageSymbolCount = 0
            }
        }
        if (sentencesInPage.isNotEmpty()) {
            pages.add(ReadingPage(sentencesInPage))
        }
        return ReadingText(pages)
    }

    fun splitToSentences(text: String): List<ReadingSentence> {
        val sentences: MutableList<ReadingSentence> = mutableListOf()
        val p = Pattern.compile(Patterns.TEXT_CORRECT_SYMBOLS)
        val formattedText = text.trim() + Constants.DOT
        val m = p.matcher(formattedText)
        while (m.find()) {
            val sentence = m.group().trim()
            if (isCorrectSentence(sentence)) sentences.add(ReadingSentence(sentence))
        }
        return sentences
    }

    private fun isCorrectSentence(sentence: String): Boolean {
        if (sentence.length <= 1) return false
        return true
    }

    fun getSentenceIndexInText(readingText: ReadingText, currentSentence: ReadingSentence): Int {
        var sentenceNumber = 0
        readingText.pages.forEach { page ->
            page.sentences.forEach { sentence ->
                if (sentence === currentSentence) {
                    return sentenceNumber
                }
                sentenceNumber++
            }
        }
        return sentenceNumber - 1
    }

    fun getPageBySentenceIndex(readingText: ReadingText, currentPageIndex: Int): ReadingPage {
        var sentenceNumber = 0
        readingText.pages.forEach { page ->
            page.sentences.forEach {
                if (currentPageIndex == sentenceNumber) return page
                sentenceNumber++
            }
        }
        throw IllegalArgumentException("Wrong sentence index")
    }
}