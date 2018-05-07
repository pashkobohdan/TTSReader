package com.pashkobohdan.ttsreader.ui.fragments.book.reading

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.TTSReaderProApplication
import com.pashkobohdan.ttsreader.mvp.bookRead.BookPresenter
import com.pashkobohdan.ttsreader.mvp.bookRead.view.BookView
import com.pashkobohdan.ttsreader.ui.dialog.DialogUtils
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractScreenFragment
import com.pashkobohdan.ttsreader.ui.listener.EmptyOnSeekBarChangeListener
import com.pashkobohdan.ttsreader.utils.listeners.EmptyUtteranceProgressListener


class BookFragment : AbstractScreenFragment<BookPresenter>(), BookView {

    private val TTS_CHECK_CODE_REQUEST_CODE = 1001

    private val HINTS_TIME_MILLIS = 2000L
    private val SPEED_SEEK_BAR_MIN_VALUE = 1
    private val SPEED_SEEK_BAR_MAX_VALUE = 500
    private val PITCH_SEEK_BAR_MIN_VALUE = 1
    private val PITCH_SEEK_BAR_MAX_VALUE = 200
    private val DIVIDE_TTS_SPEECH_RATE_BY = 100.0f
    private val DIVIDE_TTS_PITCH_RATE_BY = 100.0f

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
    @BindView(R.id.speed_rate_setting)
    lateinit var speedSeekBar: SeekBar
    @BindView(R.id.pitch_setting)
    lateinit var pitchSeekBar: SeekBar

//    @BindView(R.id.text_pager)
//    lateinit var pager: ViewPager
//    private var bookPagerAdapter: BookPagerAdapter? = null

    val textToSpeech: TextToSpeech by lazy {
        val newTextToSpeech = TextToSpeech(context?.applicationContext, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.ERROR) {
                presenter.ttsReaderInitError()
            } else {
                presenter.ttsReaderInitSuccessfully()
            }
        })
        newTextToSpeech.setOnUtteranceProgressListener(object : EmptyUtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                runInUiThread { presenter.speechDone(utteranceId) }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                runInUiThread { presenter.speechError(utteranceId, errorCode) }
            }
        })
        newTextToSpeech
    }

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

    @ProvidePresenter
    fun createSamplePresenter(): BookPresenter {
        val providePresenter = presenterProvider.get()
        providePresenter.init(data as Int)
        return providePresenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TTSReaderProApplication.INSTANCE.applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_book, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        speedSeekBar.max = SPEED_SEEK_BAR_MAX_VALUE
        speedSeekBar.setOnSeekBarChangeListener(object : EmptyOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                presenter.speedChanged(progress + SPEED_SEEK_BAR_MIN_VALUE)
            }
        })
        pitchSeekBar.max = PITCH_SEEK_BAR_MAX_VALUE
        pitchSeekBar.setOnSeekBarChangeListener(object : EmptyOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                presenter.pitchChanged(progress + PITCH_SEEK_BAR_MIN_VALUE)
            }
        })
//        bookPagerAdapter = BookPagerAdapter(fragmentManager, ArrayList()) { s ->
//            //TODO !
//        }
//        pager!!.adapter = bookPagerAdapter
    }

    override fun initTtsReader() {
        val initedTTS = textToSpeech
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

    override fun startPagesMode() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun endPagesMode() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    val speechParams: HashMap<String, String> by lazy {
        hashMapOf(Pair(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId"))
    }

    override fun speechText(text: String, speechRate: Int, pitchRate: Int) {
        textToSpeech.setSpeechRate(speechRate / DIVIDE_TTS_SPEECH_RATE_BY)
        textToSpeech.setPitch(pitchRate / DIVIDE_TTS_PITCH_RATE_BY)
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, speechParams)
    }

    override fun stopSpeeching() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
    }

    override fun onPause() {
        presenter.saveBookInfo()
        super.onPause()
    }

    override fun onDestroy() {
        stopSpeeching();
        textToSpeech.shutdown();
        super.onDestroy()
    }

    companion object {

        fun getNewInstance(bookId: Int): BookFragment {
            return AbstractScreenFragment.saveData(BookFragment(), bookId)
        }
    }
}
