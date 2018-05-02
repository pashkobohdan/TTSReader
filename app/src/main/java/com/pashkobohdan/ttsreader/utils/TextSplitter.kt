package com.pashkobohdan.ttsreader.utils

import java.util.regex.Pattern

class TextSplitter private constructor() {

    companion object {

        private val SENTENCES_REGEX = "[\\w,;'\"\\s]+[.?!:]"

        fun splitToSentences(text: String): List<String> {
            val sentences: MutableList<String> = mutableListOf()
            val p = Pattern.compile(SENTENCES_REGEX)
            val formattedText = text.trim() + Constants.DOT
            val m = p.matcher(formattedText)
            while (m.find()) {
                val sentence = m.group()
                if (sentence.length > 0) sentences.add(sentence)
            }
            return sentences
        }

        fun sentencesCount(text: String): Int {
            var sentencesCount: Int = 0
            val p = Pattern.compile(SENTENCES_REGEX)
            val formattedText = text.trim() + Constants.DOT
            val m = p.matcher(formattedText)
            while (m.find()) {
                val sentence = m.group()
                if (sentence.length > 0) sentencesCount++
            }
            return sentencesCount
        }
    }
}