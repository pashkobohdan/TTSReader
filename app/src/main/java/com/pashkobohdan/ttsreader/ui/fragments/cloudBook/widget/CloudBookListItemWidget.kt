package com.pashkobohdan.ttsreader.ui.fragments.cloudBook.widget

import android.view.LayoutInflater
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.data.cloud.model.CloudBookInfo
import com.pashkobohdan.ttsreader.databinding.WidgetCloudBookListItemBinding
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractListItemWidget
import rx.functions.Action1
import javax.inject.Inject

class CloudBookListItemWidget @Inject constructor() : AbstractListItemWidget<CloudBookInfo>() {

    private var showInfo: (CloudBookInfo) -> Unit = {}
    private var download: (CloudBookInfo) -> Unit = {}

    @Optional
    @OnClick(R.id.showInfo)
    fun showInfoClick() = showInfo(item)

    @Optional
    @OnClick(R.id.download)
    fun downloadClick() = download(item)

    override fun getHolder(parent: ViewGroup, okClickCallback: Action1<CloudBookInfo>): AbstractListItemHolder<CloudBookInfo> {
        val binding = WidgetCloudBookListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ButterKnife.bind(this, binding.root)
        return AbstractListItemHolder(binding.root, okClickCallback, Action1 { bookDTO ->
            this.item = bookDTO
            binding.book = bookDTO
        })
    }

    fun getHolder(parent: ViewGroup, okClickCallback: (CloudBookInfo) -> Unit, showInfoCallback: (CloudBookInfo) -> Unit, downloadCallback: (CloudBookInfo) -> Unit): AbstractListItemHolder<CloudBookInfo> {
        val holder = getHolder(parent, Action1 { okClickCallback(it) })
        this.showInfo = showInfoCallback
        this.download = downloadCallback
        return holder
    }
}