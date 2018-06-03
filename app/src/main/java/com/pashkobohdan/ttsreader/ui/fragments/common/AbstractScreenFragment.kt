package com.pashkobohdan.ttsreader.ui.fragments.common

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.arellomobile.mvp.MvpAppCompatFragment
import com.pashkobohdan.ttsreader.R
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
    lateinit var abstractPresenter: T

    @BindView(R.id.header_left_view)
    @JvmField
    var leftHeaderContainer: ViewGroup? = null
    @BindView(R.id.header_right_view)
    @JvmField
    var rightHeaderContainer: ViewGroup? = null
    @BindView(R.id.header_title)
    @JvmField
    var headerTitle: TextView? = null

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
        this.abstractPresenter = presenter
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

    protected fun addLeftHeaderView(view: View) {
        leftHeaderContainer?.addView(view)
    }

    protected fun addRightHeaderView(view: View) {
        rightHeaderContainer?.addView(view)
    }

    protected fun setRightHeaderView(view: View) {
        cleanRightHeaderContainer()
        rightHeaderContainer?.addView(view)
    }

    protected fun cleanRightHeaderContainer() {
        rightHeaderContainer?.removeAllViews()
    }

    protected fun setHeaderTitle(text: String) {
        headerTitle?.setText(text)
    }

    protected fun createBackHeaderButton(callback: () -> Unit = this::onBackNavigation): View {
        val back = LayoutInflater.from(context).inflate(R.layout.header_back_action, null)
        back.setOnClickListener({ callback() })
        return back
    }

    protected fun createImageHeaderButton(image: Int, callback: () -> Unit): View {
        val back = LayoutInflater.from(context).inflate(R.layout.header_image_action, null)
        val imageView = back.findViewById<ImageView>(R.id.header_button_image)
        imageView.setImageResource(image)
        imageView.setOnClickListener({ callback() })
        return back
    }

    fun onBackNavigation() {
        abstractPresenter.backNavigation()
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

    fun runInUiThread(run: () -> Unit) {
        uiHandler.post(run)
    }
}
