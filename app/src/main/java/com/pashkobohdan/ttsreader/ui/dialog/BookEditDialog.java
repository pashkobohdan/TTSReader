package com.pashkobohdan.ttsreader.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pashkobohdan.ttsreader.R;
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO;
import com.pashkobohdan.ttsreader.utils.Constants;
import com.pashkobohdan.ttsreader.utils.NullUtils;
import com.pashkobohdan.ttsreader.utils.ValidationResult;
import com.pashkobohdan.ttsreader.utils.ui.DialogUtils;
import com.pashkobohdan.ttsreader.utils.ui.LabelUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action0;
import rx.functions.Action1;

public class BookEditDialog {
    private Context context;
    private BookDTO bookDTO;
    private Action1<BookDTO> okCallback;
    private Action0 cancelCallback;

    @BindView(R.id.book_edit_dialog_book_name)
    EditText nameEditText;
    @BindView(R.id.book_edit_dialog_book_author)
    EditText authorEditText;
    @BindView(R.id.book_edit_dialog_book_text)
    EditText textEditText;
    @BindView(R.id.book_edit_dialog_book_name_layout)
    TextInputLayout bookNameLayout;
    @BindView(R.id.book_edit_dialog_book_author_layout)
    TextInputLayout bookAuthorLayout;
    @BindView(R.id.book_edit_dialog_book_text_layout)
    TextInputLayout bookTextLayout;

    public BookEditDialog(Context context, BookDTO bookDTO, Action1<BookDTO> okCallback, Action0 cancelCallback) {
        this.context = context;
        this.bookDTO = bookDTO;
        this.okCallback = okCallback;
        this.cancelCallback = cancelCallback;
    }

    public void show() {
        LayoutInflater factory = LayoutInflater.from(context);
        View dialogInputView = factory.inflate(R.layout.dialog_edit_book, null);
        ButterKnife.bind(this, dialogInputView);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(bookDTO == null ? R.string.book_creating : R.string.book_editing)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    cancelCallback.call();
                    dialog.dismiss();
                })
                .setOnCancelListener(dialog -> {
                    cancelCallback.call();
                    dialog.dismiss();
                })
                .setView(dialogInputView);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(view -> tryCreateBook(dialog));

            Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            cancelButton.setOnClickListener(v -> {
                DialogUtils.showConfirm(bookDTO == null ? R.string.book_creating_dialog_cancel_confirm : R.string.book_editing_dialog_cancel_confirm,
                        context, () -> {
                            cancelCallback.call();
                            dialog.dismiss();
                        });
            });
        });


        dialog.show();
    }

    private void tryCreateBook(DialogInterface currentDialog) {
        clearErrors();

        ValidationResult validationResult = validate();
        if (validationResult != ValidationResult.Ok) {
            showValidationError(validationResult);
        } else {
            if (bookDTO == null) {
                String text = textEditText.getText().toString();
                int length = text.split(" ").length;
                bookDTO = new BookDTO(nameEditText.getText().toString(), authorEditText.getText().toString(),
                        text, length, Constants.ZERO, new Date(), new Date());
            } else {
                bookDTO.setName(nameEditText.getText().toString());
                bookDTO.setAuthor(authorEditText.getText().toString());
                bookDTO.setText(textEditText.getText().toString());
            }

            okCallback.call(bookDTO);
            currentDialog.dismiss();
        }
    }

    private ValidationResult validate() {
        if (NullUtils.isEmptyStringWithTrim(nameEditText.getText().toString())) {
            return ValidationResult.EMPTY_BOOK_NAME;
        } else if (NullUtils.isEmptyStringWithTrim(textEditText.getText().toString())) {
            return ValidationResult.EMPTY_BOOK_TEXT;
        } else {
            return ValidationResult.Ok;
        }
    }

    private void showValidationError(ValidationResult result) {
        switch (result) {
            case EMPTY_BOOK_NAME:
                bookNameLayout.setError(LabelUtils.getValidationTextInputLayoutError(context, result));
                break;
            case EMPTY_BOOK_TEXT:
                bookTextLayout.setError(LabelUtils.getValidationTextInputLayoutError(context, result));
                break;
        }
    }

    private void clearErrors() {
        bookNameLayout.setErrorEnabled(false);
        bookAuthorLayout.setErrorEnabled(false);
        bookTextLayout.setErrorEnabled(false);
    }
}
