package com.pashkobohdan.ttsreader.ui.fragments.book;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pashkobohdan.ttsreader.ui.fragments.book.page.BookPageFragment;

import java.util.List;

import rx.functions.Action1;

public class BookPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> textPages;
    private Action1<String> selectSentenceCallback;

    public BookPagerAdapter(FragmentManager fm, @NonNull List<String> textPages, @NonNull Action1<String> selectSentenceCallback) {
        super(fm);
        this.textPages = textPages;
        this.selectSentenceCallback = selectSentenceCallback;
    }

    public void setTextPages(List<String> textPages) {
        this.textPages = textPages;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return BookPageFragment.getNewInstance(textPages.get(position), selectSentenceCallback);
    }

    @Override
    public int getCount() {
        return textPages.size();
    }
}
