package com.pashkobohdan.ttsreader.utils.ui;

import android.content.Context;

import com.pashkobohdan.ttsreader.R;
import com.pashkobohdan.ttsreader.utils.ValidationResult;

public class LabelUtils {
    private LabelUtils() {
        //closed constructor
    }

    public static String getValidationTextInputLayoutError(Context context, ValidationResult result) {
        switch (result) {
            case EMPTY_BOOK_NAME:
                return context.getString(R.string.enter_non_emty_book_title);
            case EMPTY_BOOK_TEXT:
                return context.getString(R.string.enter_non_emty_book_text);

            default: return context.getString(R.string.unknown_error);
        }
    }
}
