package com.pashkobohdan.ttsreader.ui.fragments.book.reading

import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.TTSReaderProApplication
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.bookRead.BookPresenter
import com.pashkobohdan.ttsreader.mvp.bookRead.view.BookView
import com.pashkobohdan.ttsreader.ui.fragments.book.BookPagerAdapter
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractScreenFragment
import com.pashkobohdan.ttsreader.utils.listeners.EmptyUtteranceProgressListener


class BookFragment : AbstractScreenFragment<BookPresenter>(), BookView {

    private val HINTS_TIME_MILLIS = 2000L;

    @InjectPresenter
    lateinit var presenter: BookPresenter

    @BindView(R.id.current_book_waiter)
    lateinit var progressBar: ProgressBar
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
//    @BindView(R.id.text_pager)
//    lateinit var pager: ViewPager

    private var bookPagerAdapter: BookPagerAdapter? = null

    @OnClick(R.id.current_book_back_button)
    fun backClick() = presenter.back()

    @OnClick(R.id.current_book_play_button)
    fun playClick() = presenter.play()

    @OnClick(R.id.current_book_pause_button)
    fun pauseClick() = presenter.pause()

    @OnClick(R.id.current_book_next_button)
    fun nextClick() = presenter.next()

    @OnClick(R.id.current_book_hints_container)
    fun hintsClick() = presenter.play()

    @OnClick(R.id.current_book_content_container)
    fun contentClick() = pauseClick()

    @ProvidePresenter
    fun createSamplePresenter(): BookPresenter {
        val providePresenter = presenterProvider.get()
        providePresenter.init(data as BookDTO)
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
//        bookPagerAdapter = BookPagerAdapter(fragmentManager, ArrayList()) { s ->
//            //TODO !
//        }
//        pager!!.adapter = bookPagerAdapter
    }


    override fun setText(beforeText: String, nowReadingText: String, afterText: String) {
        beforeTextView.setText(beforeText)
        currentTextView.setText(nowReadingText)
        afterTextView.setText(afterText)
    }

    override fun readText(nowReadingText: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startPagesMode() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun endPagesMode() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showEmptyBookError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress() {
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar!!.visibility = View.GONE
    }

//    override fun setText(pages: List<String>) {
//        bookPagerAdapter!!.setTextPages(pages)
//    }
//
//    override fun openPage(page: Int) {
//        pager!!.setCurrentItem(page, true)
//    }

    override fun showHints() {
        hintsContainer.visibility = View.VISIBLE
        contentContainer.alpha = 0.1f
        Handler().postDelayed({
            hintsContainer.visibility = View.GONE
            contentContainer.alpha = 1.0f
        }, HINTS_TIME_MILLIS)
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

    val textToSpeech: TextToSpeech by lazy {
        val newTextToSpeech = TextToSpeech(context?.applicationContext, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.ERROR) {
                //TODO show error
            } else {
//                t1.setLanguage(Locale.UK)
            }
        })
        newTextToSpeech.setOnUtteranceProgressListener(object : EmptyUtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                presenter.speechDone(utteranceId)
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                presenter.speechError(utteranceId, errorCode)
            }
        })

        return@lazy newTextToSpeech
    }

    val speechParams: HashMap<String, String> by lazy {
        hashMapOf(Pair(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId"))
    }

    override fun speechText(text: String, speechRate: Float) {
        textToSpeech.setSpeechRate(speechRate)
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, speechParams)
    }

    override fun stopSpeeching() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
    }

    companion object {

        fun getNewInstance(bookDTO: BookDTO): BookFragment {
            return AbstractScreenFragment.saveData(BookFragment(), bookDTO)
        }
    }


}
