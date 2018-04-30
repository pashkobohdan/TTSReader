package com.pashkobohdan.ttsreader.mvp.bookRead.view;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.pashkobohdan.ttsreader.mvp.bookRead.BookPresenter;
import com.pashkobohdan.ttsreader.mvp.common.AbstractScreenView;

import java.util.List;

@StateStrategyType(SkipStrategy.class)
public interface BookView extends AbstractScreenView<BookPresenter> {

    void showProgress();

    void hideProgress();

    void setText(List<String> pages);

    void openPage(int page);
}
