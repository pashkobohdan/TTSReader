package com.pashkobohdan.ttsreader.mvp.bookRead;

import com.arellomobile.mvp.InjectViewState;
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO;
import com.pashkobohdan.ttsreader.mvp.bookRead.view.BookView;
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter;

import javax.inject.Inject;

@InjectViewState
public class BookPresenter extends AbstractPresenter<BookView> {

    private BookDTO bookDTO;

    @Inject
    public BookPresenter() {
        //For DI
    }

    public void init(BookDTO bookDTO) {
        this.bookDTO = bookDTO;
    }

    @Override
    protected void onFirstViewAttach() {
        //TODO show data !
    }
}
