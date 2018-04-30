package com.pashkobohdan.ttsreader.ui

import android.content.Context

import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.utils.ValidationResult

object LabelUtils {

    fun getValidationTextInputLayoutError(context: Context, result: ValidationResult): String {
        when (result) {
            ValidationResult.EMPTY_BOOK_NAME -> return context.getString(R.string.enter_non_emty_book_title)
            ValidationResult.EMPTY_BOOK_TEXT -> return context.getString(R.string.enter_non_emty_book_text)

            else -> return context.getString(R.string.unknown_error)
        }
    }
}
