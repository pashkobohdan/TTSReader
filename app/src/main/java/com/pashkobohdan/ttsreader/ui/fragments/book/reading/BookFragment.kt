package com.pashkobohdan.ttsreader.ui.fragments.book.reading

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.TTSReaderApplication
import com.pashkobohdan.ttsreader.annotations.IsProVersion
import com.pashkobohdan.ttsreader.mvp.bookRead.BookPresenter
import com.pashkobohdan.ttsreader.mvp.bookRead.view.BookView
import com.pashkobohdan.ttsreader.service.SpeechService
import com.pashkobohdan.ttsreader.ui.dialog.DialogUtils
import com.pashkobohdan.ttsreader.ui.fragments.BookPagerAdapter
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractScreenFragment
import com.pashkobohdan.ttsreader.ui.listener.EmptyOnSeekBarChangeListener
import com.pashkobohdan.ttsreader.utils.Constants
import com.pashkobohdan.ttsreader.utils.TextSplitter
import javax.inject.Inject


class BookFragment : AbstractScreenFragment<BookPresenter>(), BookView {

    private val TTS_CHECK_CODE_REQUEST_CODE = 1001
    private val HINTS_TIME_MILLIS = 2000L
    private val SPEED_SEEK_BAR_MIN_VALUE = 1
    private val SPEED_SEEK_BAR_MAX_VALUE = 500
    private val PITCH_SEEK_BAR_MIN_VALUE = 1
    private val PITCH_SEEK_BAR_MAX_VALUE = 200

    @InjectPresenter
    lateinit var presenter: BookPresenter

    @BindView(R.id.current_book_settings_container)
    lateinit var settingsContainer: View
    @BindView(R.id.current_book_navigation_container)
    lateinit var navigationContainer: View
    @BindView(R.id.current_book_hints_container)
    lateinit var hintsContainer: View
    @BindView(R.id.current_book_content_container)
    lateinit var contentContainer: View
    @BindView(R.id.current_book_before_text)
    lateinit var beforeTextView: TextView
    @BindView(R.id.current_book_current_text)
    lateinit var currentTextView: TextView
    @BindView(R.id.current_book_after_text)
    lateinit var afterTextView: TextView
    @BindView(R.id.current_book_play_button)
    lateinit var playButton: View
    @BindView(R.id.current_book_pause_button)
    lateinit var pauseButton: View
    @BindView(R.id.current_book_book_mode_container)
    lateinit var bookModePagesContainer: View
    @BindView(R.id.current_book_book_mode_root)
    lateinit var bookModeRoot: ViewPager
    @BindView(R.id.current_book_reading_mode_root)
    lateinit var readingModeRoot: View
    @BindView(R.id.current_book_book_mode_current_page)
    lateinit var currentPageInput: EditText
    @BindView(R.id.current_book_book_mode_page_count)
    lateinit var pageCountTextView: TextView
    @BindView(R.id.speed_rate_setting)
    lateinit var speedSeekBar: SeekBar
    @BindView(R.id.pitch_setting)
    lateinit var pitchSeekBar: SeekBar

    var ttsConnection: ServiceConnection? = null

    @OnClick(R.id.current_book_back_button)
    fun backClick() = presenter.back()

    @OnClick(R.id.current_book_play_button)
    fun playClick() = presenter.play()

    @OnClick(R.id.current_book_pause_button)
    fun pauseClick() = presenter.pause()

    @OnClick(R.id.current_book_next_button)
    fun nextClick() = presenter.next()

    @OnClick(R.id.current_book_hints_container)
    fun hintsClick() = presenter.hideHints()

    @OnClick(R.id.current_book_content_container)
    fun contentClick() = pauseClick()

    @OnClick(R.id.current_book_book_mode)
    fun bookModeClick() {
        presenter.startBookPageMode()
    }

    @OnClick(R.id.speed_value_plus)
    fun speedPlusClick() {
        val newValue = Math.min(SPEED_SEEK_BAR_MAX_VALUE, speedSeekBar.progress + 5)
        speedSeekBar.progress = newValue
        presenter.speedChanged(newValue)
    }

    @OnClick(R.id.speed_value_minus)
    fun speedMinusClick() {
        val newValue = Math.max(SPEED_SEEK_BAR_MIN_VALUE, speedSeekBar.progress - 5)
        speedSeekBar.progress = newValue
        presenter.speedChanged(newValue)
    }

    @OnClick(R.id.pitch_value_plus)
    fun pitchPlusClick() {
        val newValue = Math.min(PITCH_SEEK_BAR_MAX_VALUE, pitchSeekBar.progress + 5)
        pitchSeekBar.progress = newValue
        presenter.pitchChanged(newValue)
    }

    @OnClick(R.id.pitch_value_minus)
    fun pitchMinusClick() {
        val newValue = Math.max(PITCH_SEEK_BAR_MIN_VALUE, pitchSeekBar.progress - 5)
        pitchSeekBar.progress = newValue
        presenter.pitchChanged(newValue)
    }

    @ProvidePresenter
    fun createSamplePresenter(): BookPresenter {
        val providePresenter = presenterProvider.get()
        providePresenter.init(data as Long)
        return providePresenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TTSReaderApplication.INSTANCE.getApplicationComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_book, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    @JvmField
    @Inject
    @IsProVersion
    var  isProVersion :Boolean= false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        speedSeekBar.max = SPEED_SEEK_BAR_MAX_VALUE
        speedSeekBar.setOnSeekBarChangeListener(object : EmptyOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    presenter.speedChanged(progress + SPEED_SEEK_BAR_MIN_VALUE)
                }
            }
        })
        pitchSeekBar.max = PITCH_SEEK_BAR_MAX_VALUE
        pitchSeekBar.setOnSeekBarChangeListener(object : EmptyOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    presenter.pitchChanged(progress + PITCH_SEEK_BAR_MIN_VALUE)
                }
            }
        })

        addLeftHeaderView(createBackHeaderButton())

        currentPageInput.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                presenter.goToPage(currentPageInput.text.toString().toInt())
                return@OnEditorActionListener true;
            }
            return@OnEditorActionListener  false;
        })
    }

    override fun setBookTitle(title: String) {
        setHeaderTitle(title)
    }

    override fun initTtsReader() {
        ttsConnection = object : ServiceConnection {

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as SpeechService.TTSBinder
                presenter.serviceCreated(binder.service)
                //TODO
            }

            override fun onServiceDisconnected(name: ComponentName) {
                //TODO
            }
        }

        val ttsIntent = Intent(activity, SpeechService::class.java)
        ttsIntent.setAction(Constants.STARTFOREGROUND_ACTION);

        activity?.bindService(ttsIntent, ttsConnection, Context.BIND_AUTO_CREATE)
        activity?.startService(ttsIntent)
    }

    override fun setText(beforeText: String, nowReadingText: String, afterText: String) {
        beforeTextView.setText(beforeText)
        currentTextView.setText(nowReadingText)
        afterTextView.setText(afterText)
    }

    override fun initSpeedAndPitch(speed: Int, pitch: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            speedSeekBar.setProgress(speed, true)
            pitchSeekBar.setProgress(pitch, true)
        } else {
            speedSeekBar.setProgress(speed)
            pitchSeekBar.setProgress(pitch)
        }
    }

    override fun selectPage(page: Int) {
        bookModeRoot.setCurrentItem(page)
    }

    override fun startPagesMode() {
        setRightHeaderView(createImageHeaderButton(R.drawable.action_done, {
            presenter.pageSelected(currentPageInput.text.toString().toInt())
        }))
        bookModePagesContainer.visibility = View.VISIBLE
        navigationContainer.visibility = View.GONE
        settingsContainer.visibility = View.GONE
        bookModeRoot.visibility = View.VISIBLE
        readingModeRoot.visibility = View.GONE
    }

    override fun endPagesMode() {
        cleanRightHeaderContainer()
        bookModePagesContainer.visibility = View.GONE
        navigationContainer.visibility = View.VISIBLE
        settingsContainer.visibility = View.VISIBLE
        bookModeRoot.visibility = View.GONE
        readingModeRoot.visibility = View.VISIBLE
    }

    override fun setPagesText(bookPageInfo: TextSplitter.Companion.BookPagesInfo) {
        currentPageInput.setText((bookPageInfo.currentPage + 1).toString())

        pageCountTextView.setText(bookPageInfo.text.size.toString())
        bookModeRoot.adapter = BookPagerAdapter(
                context ?: throw IllegalStateException("Context is null"),
                bookPageInfo.text)
        bookModeRoot.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                currentPageInput.setText((position + 1).toString())
            }

        })
        bookModeRoot.setCurrentItem(bookPageInfo.currentPage)
    }

    override fun showEmptyBookError() {
        DialogUtils.showAlert("Error", "Book is empty", context
                ?: throw IllegalStateException("Context is null"), { })
    }

    override fun showEndOfBookAlert() {
        DialogUtils.showAlert("Error", "Book is end. Start the book again ?", context
                ?: throw IllegalStateException("Context is null"), presenter::readBookFromStart, { })
    }

    override fun showStartOfBookAlert() {
        DialogUtils.showAlert("Error", "This is start of book", context
                ?: throw IllegalStateException("Context is null"), { })
    }

    override fun showTtsReaderInitError() {
        DialogUtils.showAlert("Error", "Cannot init TTS reader. Install TTS from google play and try again later", context
                ?: throw IllegalStateException("Context is null"), { })
    }

    override fun showBookExecutingError() {
        DialogUtils.showAlert("Error", "Book executing error. Try later", context
                ?: throw IllegalStateException("Context is null"), { })
    }

    override fun showProgress() {
        showProgressWithLock()
    }

    override fun hideProgress() {
        hideProgressWithUnlock()
    }

    override fun showHints() {
        hintsContainer.visibility = View.VISIBLE
        contentContainer.alpha = 0.1f
        Handler().postDelayed({
            presenter.hideHints()
        }, HINTS_TIME_MILLIS)
    }

    override fun hideHints() {
        hintsContainer.visibility = View.GONE
        contentContainer.alpha = 1.0f
    }

    override fun playMode() {
        playButton.visibility = View.GONE
        pauseButton.visibility = View.VISIBLE
        contentContainer.isClickable = true
    }

    override fun pauseMode() {
        pauseButton.visibility = View.GONE
        playButton.visibility = View.VISIBLE
        contentContainer.isClickable = false
    }

    override fun onPause() {
        presenter.saveBookInfo()
        presenter.closeNotificationIfPause()
        super.onPause()
    }

    override fun onDestroy() {
        ttsConnection?.let { activity?.unbindService(ttsConnection) }
//        stopSpeeching();
//        textToSpeech.shutdown(); // TODO replace with service !
        super.onDestroy()
    }

    companion object {

        fun getNewInstance(bookId: Long): BookFragment {
            return AbstractScreenFragment.saveData(BookFragment(), bookId)
        }
    }
}
