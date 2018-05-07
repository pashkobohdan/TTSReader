package com.pashkobohdan.ttsreader.ui.fragments.common

import android.os.Bundle
import android.os.Handler
import android.os.Looper

import com.arellomobile.mvp.MvpAppCompatFragment
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter
import com.pashkobohdan.ttsreader.mvp.common.AbstractScreenView
import com.pashkobohdan.ttsreader.ui.ProgressUtil

import java.io.Serializable

import javax.inject.Inject
import javax.inject.Provider

abstract class AbstractScreenFragment<T : AbstractPresenter<*>> : MvpAppCompatFragment(), AbstractScreenView<T> {

    @Inject
    lateinit var presenterProvider: Provider<T>
    @Inject
    lateinit var progressUtil: ProgressUtil

    //    @Inject
//    protected var abstractPresenter: T

    protected val data: Serializable?
    get() {
        val args = arguments
        return if (args != null) {
            args.getSerializable(DATA_KEY)
        } else {
            null
        }
    }

    val uiHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    protected val arrayData: Serializable?
        get() {
            val args = arguments
            if (args != null) {
                val arrayData = arrayOfNulls<Serializable>(args.size())
                for (i in 0 until args.size()) {
                    arrayData[i] = args.getSerializable(DATA_KEY + i)
                }
                return arrayData
            } else {
                return null
            }
        }

    override fun onPresenterAttached(presenter: T) {
//        this.abstractPresenter = presenter
    }

    protected open fun showProgress() {
        progressUtil.showProgress()
    }

    protected open fun hideProgress() {
        progressUtil.hideProgress()
    }

    protected fun showProgressWithLock() {
        progressUtil.showProgressWithLock()
    }

    protected fun hideProgressWithUnlock() {
        progressUtil.hideProgressWithUnlock()
    }

    companion object {

        private val DATA_KEY = "EXTRA"

        fun <T : AbstractScreenFragment<*>> saveData(fragment: T, data: Serializable): T {
            val bundle = Bundle()
            bundle.putSerializable(DATA_KEY, data)
            fragment.arguments = bundle
            return fragment
        }

        fun <T : AbstractScreenFragment<*>> saveData(fragment: T, data: Array<Serializable>): T {
            val bundle = Bundle()
            for (i in data.indices) {
                bundle.putSerializable(DATA_KEY + i, data)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    fun runInUiThread(run : ()->Unit) {
        uiHandler.post(run)
    }
}
