package com.pashkobohdan.ttsreader.ui.dialog

import android.app.Dialog
import android.content.Context
import android.support.annotation.StringRes
import butterknife.ButterKnife
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.utils.Constants

class CustomDialog private
constructor(context: Context,
            val title: String,
            val text: String,
            val okText: String,
            val cancelText: String,
            val isAlert: Boolean,
            val isCancellable: Boolean,
            val okCallback: () -> Unit,
            val cancelCallback: () -> Unit)
    : Dialog(context, R.style.CustomDialogTheme) {

    init {
        setContentView(R.layout.custom_dialog)
        ButterKnife.bind(this)
    }

    class Builder(val context: Context) {

        private var title: String? = null
        private var text: String? = null
        private var okText: String? = null
        private var cancelText: String? = null
        private var isAlert: Boolean = false
        private var isCancellable: Boolean = false
        private var okCallback: () -> Unit = {}
        private var cancelCallback: () -> Unit = {}

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setTitle(@StringRes titleId: Int): Builder {
            this.title = context.getString(titleId)
            return this
        }

        fun setText(text: String): Builder {
            this.text = text
            return this
        }

        fun setText(@StringRes textId: Int): Builder {
            this.text = context.getString(textId)
            return this
        }

        fun setOkText(text: String): Builder {
            this.okText = text
            return this
        }

        fun setOkText(@StringRes textId: Int): Builder {
            this.okText = context.getString(textId)
            return this
        }

        fun setCancelText(text: String): Builder {
            this.cancelText = text
            return this
        }

        fun setCancelText(@StringRes textId: Int): Builder {
            this.cancelText = context.getString(textId)
            return this
        }

        fun setIsAlert(isAlert: Boolean): Builder {
            this.isAlert = isAlert
            return this
        }

        fun setIsCancellable(isCancellable: Boolean): Builder {
            this.isCancellable = isCancellable
            return this
        }

        fun setOkCallback(okCallback: () -> Unit): Builder {
            this.okCallback = okCallback
            return this
        }

        fun setCancelCallback(cancelCallback: () -> Unit): Builder {
            this.cancelCallback = cancelCallback
            return this
        }

        fun build(): CustomDialog {
            val dialogTitle = (if (isAlert) Constants.EMPTY else title)
                    ?: throw IllegalArgumentException("Dialog title is null")
            val dialogText = text ?: throw IllegalArgumentException("Dialog text is null")
            return CustomDialog(
                    context,
                    dialogTitle,
                    dialogText,
                    okText ?: context.getString(R.string.ok),
                    cancelText ?: context.getString(R.string.cancel),
                    isAlert,
                    isCancellable,
                    okCallback,
                    cancelCallback
            )
        }

    }
}