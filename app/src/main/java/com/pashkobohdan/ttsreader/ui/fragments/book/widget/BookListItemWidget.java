package com.pashkobohdan.ttsreader.ui.fragments.book.widget;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pashkobohdan.ttsreader.R;
import com.pashkobohdan.ttsreader.databinding.WidgetBookListItemBinding;
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO;
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder;
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractListItemWidget;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class BookListItemWidget extends AbstractListItemWidget<BookDTO> {

    @BindView(R.id.book_list_item_book_progress)
    TextView progress;

    @Inject
    public BookListItemWidget() {
        //For DI
    }

    @Override
    protected AbstractListItemHolder<BookDTO> getHolder(ViewGroup parent, Action1<BookDTO> okClickCallback) {
        WidgetBookListItemBinding binding = WidgetBookListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ButterKnife.bind(this, binding.getRoot());
        return new AbstractListItemHolder<>(binding.getRoot(), okClickCallback, bookDTO -> {
            binding.setBook(bookDTO);
            progress.setText(parent.getContext().getString(R.string.book_progress_label, String.valueOf(55)));//TODO replace with calculating percentage
        });
    }

    public AbstractListItemHolder<BookDTO> getHolder(ViewGroup parent, Action1<BookDTO> okClickCallback, Action1<BookDTO> longClickCallback) {
        WidgetBookListItemBinding binding = WidgetBookListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ButterKnife.bind(this, binding.getRoot());
        return new AbstractListItemHolder<>(binding.getRoot(), okClickCallback, bookDTO -> {
            binding.setBook(bookDTO);
            binding.getRoot().setOnLongClickListener(view -> {
                longClickCallback.call(bookDTO);
                return true;
            });
            progress.setText(parent.getContext().getString(R.string.book_progress_label, String.valueOf(55)));//TODO replace with calculating percentage
        });
    }
}
