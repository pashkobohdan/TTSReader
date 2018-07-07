package com.pashkobohdan.ttsreader.ui.fragments.common

import android.view.ViewGroup

import com.pashkobohdan.ttsreader.data.model.dto.common.CommonDTO
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder

import rx.functions.Action1

abstract class AbstractListItemWidget<T : CommonDTO> {

    lateinit var item: T;

    abstract fun getHolder(parent: ViewGroup, okClickCallback: Action1<T>): AbstractListItemHolder<T>
}
