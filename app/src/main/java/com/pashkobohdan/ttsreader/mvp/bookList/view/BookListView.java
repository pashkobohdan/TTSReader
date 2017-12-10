package com.pashkobohdan.ttsreader.mvp.bookList.view;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO;
import com.pashkobohdan.ttsreader.mvp.bookList.BookListPresenter;
import com.pashkobohdan.ttsreader.mvp.common.AbstractScreenView;

import java.util.List;

/**
 * Created by bohdan on 07.08.17.
 */
@StateStrategyType(SkipStrategy.class)
public interface BookListView extends AbstractScreenView<BookListPresenter> {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showBookList(List<BookDTO> bookDTOList);

    void addBook(BookDTO bookDTO);

    void deleteBook(int deletedBookPosition);

    void showDataExecutionError();

    void showRefresh();

    void hideRefresh();
}
