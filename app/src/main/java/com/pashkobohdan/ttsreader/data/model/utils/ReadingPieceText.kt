package com.pashkobohdan.ttsreader.data.model.utils

import com.pashkobohdan.ttsreader.service.readingData.ReadingSentence

data class ReadingPieceText(val prev: String, val currentSentence: ReadingSentence, val next: String)
