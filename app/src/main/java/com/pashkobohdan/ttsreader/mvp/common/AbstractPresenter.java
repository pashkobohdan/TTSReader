package com.pashkobohdan.ttsreader.mvp.common;

import com.arellomobile.mvp.MvpPresenter;
import com.pashkobohdan.ttsreader.ui.Screen;

public class AbstractPresenter<T extends AbstractScreenView> extends MvpPresenter<T> {

    public static final String INDEX_SCREEN_KEY = Screen.BOOK_LIST;
    public static String currentScreen = "";

    @Override
    public void attachView(T view) {
        super.attachView(view);
        view.onPresenterAttached(this);
    }
}
