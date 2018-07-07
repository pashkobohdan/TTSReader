package com.pashkobohdan.ttsreader.mvp.cloudBookList.view

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.pashkobohdan.ttsreader.data.cloud.model.CloudBookInfo
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.cloudBookList.CloudBookListPresenter
import com.pashkobohdan.ttsreader.mvp.common.AbstractScreenView

@StateStrategyType(SkipStrategy::class)
interface CloudBookListView : AbstractScreenView<CloudBookListPresenter> {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showBookList(bookInfoList: List<CloudBookInfo>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideProgress()

    fun showBookListLoadError()

    fun showBookDownloadError()

    fun showBookDownloadSuccessful()

    fun bookSaveError(bookDTO : BookDTO)

    fun showBookText(title: String, text: String)
}