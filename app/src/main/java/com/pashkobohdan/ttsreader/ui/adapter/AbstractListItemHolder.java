package com.pashkobohdan.ttsreader.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pashkobohdan.ttsreader.model.dto.common.CommonDTO;

import rx.functions.Action1;

public class AbstractListItemHolder<T extends CommonDTO> extends RecyclerView.ViewHolder implements View.OnClickListener{
    private Action1<T> okClickCallback;
    private Action1<T> onBindAction;
    private T data;

    public AbstractListItemHolder(View itemView, Action1<T> okClickCallback, Action1<T> onBindAction) {
        super(itemView);
        this.okClickCallback = okClickCallback;
        this.onBindAction = onBindAction;
        itemView.setOnClickListener(this);
    }

    public void onBindViewHolder(T t) {
        this.data = t;
        onBindAction.call(t);
    }

    @Override
    public void onClick(View v) {
        if(okClickCallback != null) {
            okClickCallback.call(data);
        }
    }
}
