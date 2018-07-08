package com.pashkobohdan.ttsreader.data.storage.impl

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.pashkobohdan.ttsreader.data.storage.UserStorage
import com.pashkobohdan.ttsreader.data.storage.utils.ObjectSerializerHelper
import java.util.*
import javax.inject.Inject


class ApplicationStorage @Inject constructor() : UserStorage {

    @Inject
    lateinit var context: Context

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun getCurrentReadingLanguage(): Locale? {
        val localeString = preferences.getString(READING_LANGUAGE, "")
        if (localeString.isEmpty()) {
            return null
        }
        return ObjectSerializerHelper.stringToObject(localeString) as Locale
    }

    override fun setCurrentReadingLanguage(locale: Locale) {
        preferences.edit().putString(READING_LANGUAGE, ObjectSerializerHelper.objectToString(locale)).apply()
    }

    companion object {
        private const val READING_LANGUAGE = "READING_LANGUAGE"
    }
}