package com.pashkobohdan.ttsreader.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.ui.LabelUtils
import com.pashkobohdan.ttsreader.utils.NullUtils
import com.pashkobohdan.ttsreader.utils.TextSplitter
import com.pashkobohdan.ttsreader.utils.ValidationResult
import java.util.*

class BookEditDialog(val context: Context, var bookDTO: BookDTO?, val okCallback: (BookDTO) -> Unit = {}, val cancelCallback: () -> Unit = {}) {

    @BindView(R.id.book_edit_dialog_book_name)
    lateinit var nameEditText: EditText
    @BindView(R.id.book_edit_dialog_book_author)
    lateinit var authorEditText: EditText
    @BindView(R.id.book_edit_dialog_book_text)
    lateinit var textEditText: EditText
    @BindView(R.id.edit_book_text_button)
    lateinit var editBookTextButton: View
    @BindView(R.id.book_edit_dialog_book_name_layout)
    lateinit var bookNameLayout: TextInputLayout
    @BindView(R.id.book_edit_dialog_book_author_layout)
    lateinit var bookAuthorLayout: TextInputLayout
    @BindView(R.id.book_edit_dialog_book_text_layout)
    lateinit var bookTextLayout: TextInputLayout

    @OnClick(R.id.edit_book_text_button)
    fun editBookTextClick() {
        textEditText.setText(bookDTO?.text)
        textEditText.isEnabled = true
        editBookTextButton.visibility = View.GONE
    }

    fun show() {
        val factory = LayoutInflater.from(context)
        val dialogInputView = factory.inflate(R.layout.dialog_edit_book, null)
        ButterKnife.bind(this, dialogInputView)

        nameEditText.setText(bookDTO?.name)
        authorEditText.setText(bookDTO?.author)
        val text = bookDTO?.text
        if(text != null) {
            val cutText = text.subSequence(0, 100)
            textEditText.setText(cutText)
            textEditText.isEnabled = false
        } else {
            editBookTextButton.visibility = View.GONE
        }


        val builder = AlertDialog.Builder(context)
                .setTitle(if (bookDTO == null) R.string.book_creating else R.string.book_editing)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel) { dialog, which ->
                    cancelCallback()
                    dialog.dismiss()
                }
                .setOnCancelListener { dialog ->
                    cancelCallback()
                    dialog.dismiss()
                }
                .setView(dialogInputView)
        val dialog = builder.create()
        dialog.setOnShowListener { dialogInterface ->
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener { view -> tryCreateBook(dialog) }

            val cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            cancelButton.setOnClickListener { v ->
                DialogUtils.showConfirm(if (bookDTO == null) R.string.book_creating_dialog_cancel_confirm else R.string.book_editing_dialog_cancel_confirm,
                        context) {
                    cancelCallback()
                    dialog.dismiss()
                }
            }
        }


        dialog.show()
    }

    private fun tryCreateBook(currentDialog: DialogInterface) {
        clearErrors()

        val validationResult = validate()
        if (validationResult != ValidationResult.Ok) {
            showValidationError(validationResult)
        } else {
            var newBook = bookDTO
            if (newBook == null) {
                val text = textEditText.text.toString()
                val length = TextSplitter.sentencesCount(text)
                newBook = BookDTO(nameEditText.text.toString(), authorEditText.text.toString(),
                        text, length, 0, 100, 100, Date(), Date())
            } else {
                newBook.name = nameEditText.text.toString()
                newBook.author = authorEditText.text.toString()
                newBook.text = textEditText.text.toString()
            }

            okCallback(newBook)
            currentDialog.dismiss()
        }
    }

    private fun validate(): ValidationResult {
        return if (NullUtils.isEmptyStringWithTrim(nameEditText.text.toString())) {
            ValidationResult.EMPTY_BOOK_NAME
        } else if (NullUtils.isEmptyStringWithTrim(textEditText.text.toString())) {
            ValidationResult.EMPTY_BOOK_TEXT
        } else {
            ValidationResult.Ok
        }
    }

    private fun showValidationError(result: ValidationResult) {
        when (result) {
            ValidationResult.EMPTY_BOOK_NAME -> bookNameLayout.error = LabelUtils.getValidationTextInputLayoutError(context, result)
            ValidationResult.EMPTY_BOOK_TEXT -> bookTextLayout.error = LabelUtils.getValidationTextInputLayoutError(context, result)
        }
    }

    private fun clearErrors() {
        bookNameLayout.isErrorEnabled = false
        bookAuthorLayout.isErrorEnabled = false
        bookTextLayout.isErrorEnabled = false
    }
}
