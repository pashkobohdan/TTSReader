package com.pashkobohdan.ttsreader.service

import com.pashkobohdan.ttsreader.data.model.utils.ReadingText

interface TtsListener {

    fun init(okCallback: () -> Unit, errorCallback: () -> Unit)

    fun loadBook(bookId: Long,
                 loadOkCallback: () -> Unit,
                 loadErrorCallback: () -> Unit,
                 bookEmptyErrorCallback: () -> Unit,
                 bookInfoCahngeCallback: (Int, Int) -> Unit)

    fun setTextReadingListener(textChange: (ReadingText) -> Unit,
                               playCallback: () -> Unit,
                               pauseCallback: () -> Unit,
                               textReadingError: () -> Unit,
                               endOfTextCallback: () -> Unit)

    fun changeBookInfo(speechRate: Int, pitchRate: Int)

    fun resume()

    fun pause()

    fun stopIfPause()

    fun saveCurrentBookInfo()

    fun prevSentence()

    fun nextSentence()
}