package com.pashkobohdan.ttsreader.ui.fragments.book;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.pashkobohdan.ttsreader.R;
import com.pashkobohdan.ttsreader.TTSReaderProApplication;
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO;
import com.pashkobohdan.ttsreader.mvp.bookRead.BookPresenter;
import com.pashkobohdan.ttsreader.mvp.bookRead.view.BookView;
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractScreenFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookFragment extends AbstractScreenFragment<BookPresenter> implements BookView {

    @InjectPresenter
    BookPresenter presenter;

    @BindView(R.id.fragment_book_waiter_mask)
    protected ProgressBar progressBar;
    @BindView(R.id.text_settings_container)
    protected View settingsContainer;
    @BindView(R.id.text_pager)
    protected ViewPager pager;

    private BookPagerAdapter bookPagerAdapter;

    @ProvidePresenter
    public BookPresenter createSamplePresenter() {
        BookPresenter providePresenter = presenterProvider.get();
        providePresenter.init((BookDTO) getData());
        return providePresenter;
    }

    public static BookFragment getNewInstance(BookDTO bookDTO) {
        return saveData(new BookFragment(), bookDTO);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        TTSReaderProApplication.INSTANCE.getApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookPagerAdapter = new BookPagerAdapter(getFragmentManager(), new ArrayList<>(), s -> {
            //TODO !
        });
        pager.setAdapter(bookPagerAdapter);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setText(List<String> pages) {
        bookPagerAdapter.setTextPages(pages);
    }

    @Override
    public void openPage(int page) {
        pager.setCurrentItem(page, true);
    }


}
