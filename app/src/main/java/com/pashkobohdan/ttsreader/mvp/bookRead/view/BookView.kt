package com.pashkobohdan.ttsreader.mvp.bookRead.view

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.pashkobohdan.ttsreader.mvp.bookRead.BookPresenter
import com.pashkobohdan.ttsreader.mvp.common.AbstractScreenView
import com.pashkobohdan.ttsreader.utils.TextSplitter

@StateStrategyType(SkipStrategy::class)
interface BookView : AbstractScreenView<BookPresenter> {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setBookTitle(title: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setText(beforeText: String, nowReadingText: String, afterText: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun initSpeedAndPitch(speed: Int, pitch: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun playMode()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun pauseMode()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideHints()

    fun showBookExecutingError()

    fun initTtsReader()

    fun showTtsReaderInitError()

    fun startPagesMode()

    fun endPagesMode()

    fun showHints()

    fun showEmptyBookError()

    fun selectPage(page: Int)

    fun setPagesText(bookPageInfo: TextSplitter.Companion.BookPagesInfo)

    fun showEndOfBookAlert()

    fun showStartOfBookAlert()
}
