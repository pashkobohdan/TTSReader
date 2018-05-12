package com.pashkobohdan.ttsreader.ui.common.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.pashkobohdan.ttsreader.data.model.dto.common.CommonDTO;
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListAdapter<T extends CommonDTO> extends RecyclerView.Adapter<AbstractListItemHolder<T>> {

    protected List<T> dataList;

    public AbstractListAdapter(List<T> arrayData) {
        this.dataList = arrayData;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addData(T data) {
        if(dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.add(data);
        notifyDataSetChanged();
    }

    @Override
    public AbstractListItemHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        return createItemHolder(parent);
    }

    @Override
    public void onBindViewHolder(AbstractListItemHolder<T> holder, int position) {
        holder.onBindViewHolder(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public abstract AbstractListItemHolder<T> createItemHolder(ViewGroup parent);
}
