package com.pashkobohdan.ttsreader.mvp.bookRead.view

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.pashkobohdan.ttsreader.mvp.bookRead.BookPresenter
import com.pashkobohdan.ttsreader.mvp.common.AbstractScreenView

@StateStrategyType(SkipStrategy::class)
interface BookView : AbstractScreenView<BookPresenter> {

    fun showProgress()

    fun hideProgress()

    fun setText(beforeText: String, nowReadingText: String, afterText: String)

    fun readText(nowReadingText: String)

    fun startPagesMode()

    fun endPagesMode()

    fun showHints()

    fun showEmptyBookError()

    fun playMode()

    fun pauseMode()

    fun speechText(text: String, speechRate:Float)

    fun stopSpeeching()
}
