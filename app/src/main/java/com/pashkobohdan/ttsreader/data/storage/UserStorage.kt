package com.pashkobohdan.ttsreader.data.storage

import java.util.*

interface UserStorage {

    fun setCurrentReadingLanguage(locale: Locale)

    fun getCurrentReadingLanguage(): Locale?
}