package com.pashkobohdan.ttsreader.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.pashkobohdan.ttsreader.R;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class DialogUtils {
    private DialogUtils() {
        //closed constructor
    }

    public static void showAlert(int titleId, int textId, Context context, @NonNull Action0 okCallback) {
        showAlert(context.getString(titleId), context.getString(textId), context, okCallback);
    }

    public static void showAlert(@Nullable String title, @Nullable String text, @NonNull Context context, @NonNull Action0 okCallback) {
        showAlert(title, text, context.getString(R.string.ok), context.getString(R.string.cancel), context, okCallback, null);
    }

    public static void showAlert(@Nullable String title, @Nullable String text, String okButtonText, String cancelButtonText, @NonNull Context context, @NonNull Action0 okCallback, @Nullable Action0 cancelCallback) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(okButtonText, (dialog, which) -> okCallback.call())
                .setNegativeButton(cancelButtonText, (dialog, which) -> cancelCallback.call())
                .show();
    }

    public static void showConfirm(int textId, Context context, @NonNull Action0 okCallback) {
        showConfirm(R.string.confirmation, textId,  context, okCallback);
    }

    public static void showConfirm(int titleId, int textId, Context context, @NonNull Action0 okCallback) {
        showConfirm(context.getString(titleId), context.getString(textId), context, okCallback);
    }

    public static void showConfirm(@Nullable String title, @Nullable String text, @NonNull Context context, @NonNull Action0 okCallback) {
        showConfirm(title, text, context.getString(R.string.ok), context.getString(R.string.cancel), context, okCallback, null);
    }

    public static void showConfirm(@Nullable String title, @Nullable String text, String okButtonText, String cancelButtonText, @NonNull Context context, @NonNull Action0 okCallback, @Nullable Action0 cancelCallback) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(okButtonText, (dialog, which) -> {
                    okCallback.call();
                })
                .setNegativeButton(cancelButtonText, (dialog, which) -> {
                    if (cancelCallback != null) {
                        cancelCallback.call();
                    }
                })
                .show();
    }

    public static <T>void showOptionsDialog(@Nullable String title, @NonNull Context context,
                                            List<T> items, Func1<T, String> titleProvier, Action1<T> callback,
                                            boolean withCancelButton) {
        List<T> titlesKeys = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for(T t : items) {
            String currentTitle = titleProvier.call(t);
            titlesKeys.add(t);
            titles.add(currentTitle);
        }
        if(withCancelButton) {
            titles.add(context.getString(R.string.cancel));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setItems(titles.toArray(new String[titlesKeys.size()]), (dialog, which) -> {
                    if(withCancelButton && which >= titles.size() - 1) {
                        dialog.dismiss();
                    } else {
                        callback.call(titlesKeys.get(which));
                    }
                });
        builder.create().show();
    }
}
