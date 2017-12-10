package com.pashkobohdan.ttsreader.ui.fragments.common;

import android.view.ViewGroup;

import com.pashkobohdan.ttsreader.model.dto.common.CommonDTO;
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder;

import rx.functions.Action1;

public abstract class AbstractListItemWidget<T extends CommonDTO> {

    public AbstractListItemWidget(){
        //Must have empty constructor
    }

    protected abstract AbstractListItemHolder<T> getHolder(ViewGroup parent, Action1<T> okClickCallback) ;
}
