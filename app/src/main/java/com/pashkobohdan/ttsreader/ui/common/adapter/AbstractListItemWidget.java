package com.pashkobohdan.ttsreader.ui.common.adapter;

import android.view.View;
import android.view.ViewGroup;

public abstract class AbstractListItemWidget<T> {

    public AbstractListItemWidget() {
    }

    public abstract View createView(ViewGroup parent);

    public abstract void bindItem(T t);
}
