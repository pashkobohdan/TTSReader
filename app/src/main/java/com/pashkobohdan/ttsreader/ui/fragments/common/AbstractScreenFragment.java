package com.pashkobohdan.ttsreader.ui.fragments.common;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter;
import com.pashkobohdan.ttsreader.mvp.common.AbstractScreenView;
import com.pashkobohdan.ttsreader.ui.activities.ToolbarHandler;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Provider;

public abstract class AbstractScreenFragment<T extends AbstractPresenter> extends MvpAppCompatFragment implements AbstractScreenView<T> {

    private static final String DATA_KEY = "EXTRA";

    @Inject
    protected Provider<T> presenterProvider;

    @Inject
    Context context;

    @Inject
    protected T presenter;

    @Override
    public void onPresenterAttached(T presenter) {
        this.presenter = presenter;
    }

    protected AbstractScreenFragment saveData(AbstractScreenFragment fragment, Serializable data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_KEY, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected AbstractScreenFragment saveArrayData(AbstractScreenFragment fragment, Serializable[] data) {
        Bundle bundle = new Bundle();
        for (int i = 0; i < data.length; i++) {
            bundle.putSerializable(DATA_KEY + i, data);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    protected Serializable getData() {
        if (getArguments() != null) {
            return getArguments().getSerializable(DATA_KEY);
        } else {
            return null;
        }
    }

    protected Serializable getArrayData() {
        if (getArguments() != null) {
            Serializable[] arrayData = new Serializable[getArguments().size()];
            for (int i = 0; i < getArguments().size(); i++) {
                arrayData[i] = getArguments().getSerializable(DATA_KEY + i);
            }
            return arrayData;
        } else {
            return null;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO delete header and back button/menus
    }

    protected void setHeaderTitle(String title) {
        if (context instanceof ToolbarHandler) {
            ((ToolbarHandler) context).setTitle(title);
        }
    }
}
