package com.pashkobohdan.ttsreader.service

import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.data.model.utils.ReadingText
import java.util.*

interface TtsListener {

    fun init(okCallback: () -> Unit, errorCallback: () -> Unit)

    fun loadBook(bookId: Long,
                 loadOkCallback: (BookDTO) -> Unit,
                 loadErrorCallback: () -> Unit,
                 bookEmptyErrorCallback: () -> Unit,
                 bookInfoCahngeCallback: (Int, Int) -> Unit)

    fun setTextReadingListener(textChange: (ReadingText) -> Unit,
                               playCallback: () -> Unit,
                               pauseCallback: () -> Unit,
                               textReadingError: () -> Unit,
                               endOfTextCallback: () -> Unit)

    fun getAvailableLanguages(): List<Locale>?

    fun changeReadingLanguage(locale: Locale)

    fun changeBookInfo(speechRate: Int, pitchRate: Int)

    fun currentPageSelected(page: Int)

    fun moveToStartOfBook()

    fun resume()

    fun pause()

    fun stopIfPause()

    fun saveCurrentBookInfo()

    fun prevSentence()

    fun nextSentence()
}