package com.pashkobohdan.ttsreader.mvp.common;

import com.arellomobile.mvp.MvpPresenter;
import com.pashkobohdan.ttsreader.ui.Screen;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

public class AbstractPresenter<T extends AbstractScreenView> extends MvpPresenter<T> {

    public static final String INDEX_SCREEN_KEY = Screen.BOOK_LIST;
    public static String currentScreen = "";

    @Inject
    protected Router router;

    @Override
    public void attachView(T view) {
        super.attachView(view);
        view.onPresenterAttached(this);
    }

    public void backNavigation() {
        router.exit();
    }
}
