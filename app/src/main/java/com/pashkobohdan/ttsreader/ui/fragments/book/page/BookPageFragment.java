package com.pashkobohdan.ttsreader.ui.fragments.book.page;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pashkobohdan.ttsreader.R;

import butterknife.ButterKnife;
import rx.functions.Action1;

public class BookPageFragment extends Fragment {

    private String text;
    private Action1<String> selectSentenceCallback;

    public static BookPageFragment getNewInstance(@NonNull String text,  @NonNull Action1<String> selectSentenceCallback) {
        BookPageFragment fragment = new BookPageFragment();
        fragment.setText(text);
        fragment.setSelectSentenceCallback(selectSentenceCallback);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_book_page, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void setText(String text) {
        this.text = text;
    }

    private void setSelectSentenceCallback(Action1<String> selectSentenceCallback) {
        this.selectSentenceCallback = selectSentenceCallback;
    }
}
