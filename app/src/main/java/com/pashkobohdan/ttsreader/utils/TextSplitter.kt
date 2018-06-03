package com.pashkobohdan.ttsreader.utils

import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import java.util.regex.Pattern

class TextSplitter private constructor() {

    companion object {

//        private val SENTENCES_REGEX = "[\\w,;'\"\\s]+[.?!:]"

        val MIN_PAGE_SYMBOLS_COUNT = 800

        fun splitToSentences(text: String): List<String> {
            val sentences: MutableList<String> = mutableListOf()
            val p = Pattern.compile(Patterns.TEXT_CORRECT_SYMBOLS)
            val formattedText = text.trim() + Constants.DOT
            val m = p.matcher(formattedText)
            while (m.find()) {
                val sentence = m.group()
                if (sentence.length > 0) sentences.add(sentence)
            }
            return sentences
        }

        fun sentencesCount(text: String): Int {
            var sentencesCount = 0
            val p = Pattern.compile(Patterns.TEXT_CORRECT_SYMBOLS)
            val formattedText = text.trim() + Constants.DOT
            val m = p.matcher(formattedText)
            while (m.find()) {
                val sentence = m.group()
                if (sentence.length > 0) sentencesCount++
            }
            return sentencesCount
        }

        fun readPages(bookDTO: BookDTO): List<List<String>> {
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

        fun getProgressByCurrentPage(bookDTO: BookDTO, page: Int):Int {
            val pages: MutableList<String> = mutableListOf()
            var sentencesInPage = StringBuilder()
            var pageSymbolCount = 0
            var sentenceNumber = 0
            for (sentence in TextSplitter.splitToSentences(bookDTO.text)) {
                sentencesInPage.append(sentence)
                pageSymbolCount += sentence.length
                if(pages.size == page) {
                    break
                }
                if (pageSymbolCount > MIN_PAGE_SYMBOLS_COUNT) {
                    pages.add(sentencesInPage.toString())
                    sentencesInPage = StringBuilder()
                    pageSymbolCount = 0

                }
                sentenceNumber++
            }
            return sentenceNumber
        }

        fun readPagesText(bookDTO: BookDTO): BookPagesInfo {
            val pages: MutableList<String> = mutableListOf()
            var sentencesInPage = StringBuilder()
            var pageSymbolCount = 0
            var sentenceNumber = 0
            var currentPage = 1
            for (sentence in TextSplitter.splitToSentences(bookDTO.text)) {
                sentenceNumber++
                sentencesInPage.append(sentence)
                pageSymbolCount += sentence.length
                if (pageSymbolCount > MIN_PAGE_SYMBOLS_COUNT) {
                    pages.add(sentencesInPage.toString())
                    sentencesInPage = StringBuilder()
                    pageSymbolCount = 0
                    if (sentenceNumber == bookDTO.progress) {
                        currentPage = pages.size
                    }
                } else if (sentenceNumber == bookDTO.progress) {
                    currentPage = pages.size
                }
            }
            if (sentencesInPage.isNotEmpty()) {
                pages.add(sentencesInPage.toString())
            }
            return BookPagesInfo(pages, currentPage)
        }

        data class BookPagesInfo(val text: List<String>, val currentPage: Int)
    }
}