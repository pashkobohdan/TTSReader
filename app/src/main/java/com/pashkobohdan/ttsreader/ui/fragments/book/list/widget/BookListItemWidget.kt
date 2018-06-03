package com.pashkobohdan.ttsreader.ui.fragments.book.list.widget

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.databinding.WidgetBookListItemBinding
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractListItemWidget
import rx.functions.Action1
import javax.inject.Inject
import kotlin.math.roundToInt

class BookListItemWidget @Inject constructor() : AbstractListItemWidget<BookDTO>() {

    @BindView(R.id.book_list_item_book_progress)
    lateinit var progress: TextView

    override fun getHolder(parent: ViewGroup, okClickCallback: Action1<BookDTO>): AbstractListItemHolder<BookDTO> {
        val binding = WidgetBookListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ButterKnife.bind(this, binding.root)
        return AbstractListItemHolder(binding.root, okClickCallback, Action1{ bookDTO ->
            binding.book = bookDTO
            progress.text = parent.context.getString(R.string.book_progress_label, 0.toString())
        })
    }

    fun getHolder(parent: ViewGroup, okClickCallback: Action1<BookDTO>, longClickCallback: Action1<BookDTO>): AbstractListItemHolder<BookDTO> {
        val binding = WidgetBookListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ButterKnife.bind(this, binding.root)
        return AbstractListItemHolder(binding.root, okClickCallback, Action1{ bookDTO ->
            binding.book = bookDTO
            binding.root.setOnLongClickListener { view ->
                longClickCallback.call(bookDTO)
                true
            }

            val progressValue = 100.0f * bookDTO.getProgress()!! / bookDTO.getLength()!!
            progress.setText(parent.context.getString(R.string.book_progress_label, progressValue.roundToInt().toString()))
        })
    }
}
