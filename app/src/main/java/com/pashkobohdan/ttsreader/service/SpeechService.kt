package com.pashkobohdan.ttsreader.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.view.View
import android.widget.RemoteViews
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.TTSReaderApplication
import com.pashkobohdan.ttsreader.data.executors.book.GetBookByIdUseCase
import com.pashkobohdan.ttsreader.data.executors.book.UpdateBookInfoUseCase
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.data.model.utils.ReadingPieceText
import com.pashkobohdan.ttsreader.data.storage.UserStorage
import com.pashkobohdan.ttsreader.data.usecase.observers.DefaultObserver
import com.pashkobohdan.ttsreader.mvp.bookRead.BookPresenter
import com.pashkobohdan.ttsreader.service.readingData.ReadingData
import com.pashkobohdan.ttsreader.service.readingData.ReadingPage
import com.pashkobohdan.ttsreader.service.readingData.ReadingSentence
import com.pashkobohdan.ttsreader.service.readingData.ReadingText
import com.pashkobohdan.ttsreader.ui.activities.MainActivity
import com.pashkobohdan.ttsreader.utils.Constants
import com.pashkobohdan.ttsreader.utils.listeners.EmptyUtteranceProgressListener
import java.util.*
import javax.inject.Inject

class SpeechService : Service(), TtsListener {

    @Inject
    lateinit var getBookListUseCase: GetBookByIdUseCase
    @Inject
    lateinit var updateBookInfoUseCase: UpdateBookInfoUseCase
    @Inject
    lateinit var userStorage: UserStorage

    private lateinit var bookDTO: BookDTO
    private var bookId: Long = -1
    private var readingState: BookPresenter.READING_STATE = BookPresenter.READING_STATE.PAUSE

    private lateinit var readingText: ReadingText
    private lateinit var readingPage: ReadingPage
    private lateinit var readingSentence: ReadingSentence

    private lateinit var textToSpeech: TextToSpeech
    private var textChange: (ReadingPieceText) -> Unit = {}
    private var playCallback: () -> Unit = {}
    private var pauseCallback: () -> Unit = {}
    private var textReadingError: () -> Unit = {}
    private var endOfTextCallback: () -> Unit = {}
    private var bookInfoCahngeCallback: (Int, Int) -> Unit = { _, _ -> }
    private var speechRate: Int = 0
    private var pitchRate: Int = 0

    private var isNotificationShowed: Boolean = false
    private var serviceSuccessInited: Boolean = false

    private lateinit var handler: Handler

    inner class TTSBinder : Binder() {
        internal val service: SpeechService
            get() = this@SpeechService
    }

    val speechParams: HashMap<String, String> by lazy {
        hashMapOf(Pair(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId"))
    }

    override fun onBind(arg0: Intent): IBinder? {
        return TTSBinder()
    }

    override fun onCreate() {
        TTSReaderApplication.INSTANCE.getApplicationComponent().inject(this)
        super.onCreate()

        handler = Handler()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == Constants.PREV_ACTION) {
            prevSentence()
        } else if (intent.action == Constants.PLAY_ACTION) {
            resume()
        } else if (intent.action == Constants.PAUSE_ACTION) {
            pause()
        } else if (intent.action == Constants.NEXT_ACTION) {
            nextSentence()
        } else if (intent.action == Constants.STOPFOREGROUND_ACTION) {
            stopForeground(true)
            stopSelf()
        }
        return Service.START_STICKY
    }

    val remoteViews: RemoteViews by lazy {
        val views = RemoteViews(packageName, R.layout.status_bar)

        val previousIntent = Intent(this, SpeechService::class.java)
        previousIntent.action = Constants.PREV_ACTION
        val ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0)

        val playIntent = Intent(this, SpeechService::class.java)
        playIntent.action = Constants.PLAY_ACTION
        val pplayIntent = PendingIntent.getService(this, 0, playIntent, 0)

        val pauseIntent = Intent(this, SpeechService::class.java)
        pauseIntent.action = Constants.PAUSE_ACTION
        val ppauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0)

        val nextIntent = Intent(this, SpeechService::class.java)
        nextIntent.action = Constants.NEXT_ACTION
        val pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0)

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent)
        views.setOnClickPendingIntent(R.id.status_bar_pause, ppauseIntent)
        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent)
        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent)
        if (readingState == BookPresenter.READING_STATE.READING) {
            views.setViewVisibility(R.id.status_bar_play, View.GONE)
            views.setViewVisibility(R.id.status_bar_pause, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.status_bar_play, View.VISIBLE)
            views.setViewVisibility(R.id.status_bar_pause, View.GONE)
        }

        return@lazy views
    }

    val notificationBuilder: NotificationCompat.Builder by lazy {

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.action = Constants.OPEN_BOOK_ACTION
        notificationIntent.putExtra(Constants.OPEN_BOOK_ACTION_BOOK_ID_KEY, bookId)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0)

        val closeIntent = Intent(this, SpeechService::class.java)
        closeIntent.action = Constants.STOPFOREGROUND_ACTION
        val pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0)

        val mBuilder = NotificationCompat.Builder(this, NOTOFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(pcloseIntent)
        createNotificationChannel()

        return@lazy mBuilder
    }

    val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(this)
    }

    private fun updateNotification() {
        if (readingState == BookPresenter.READING_STATE.READING) {
            remoteViews.setViewVisibility(R.id.status_bar_play, View.GONE)
            remoteViews.setViewVisibility(R.id.status_bar_pause, View.VISIBLE)
            notificationBuilder.setOngoing(true)
        } else {
            remoteViews.setViewVisibility(R.id.status_bar_play, View.VISIBLE)
            remoteViews.setViewVisibility(R.id.status_bar_pause, View.GONE)
            notificationBuilder.setOngoing(false)
        }

        remoteViews.setTextViewText(R.id.status_bar_book_author, bookDTO.author)
        remoteViews.setTextViewText(R.id.status_bar_book_name, bookDTO.name)

        notificationManager.notify(NOTOFICATION_ID, notificationBuilder.build())
        isNotificationShowed = true
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTOFICATION_CHANNEL_ID, NOTOFICATION_CHANNEL_NAME, importance)
            channel.description = NOTOFICATION_CHANNEL_DESKR

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    override fun init(okCallback: () -> Unit, errorCallback: () -> Unit) {
        if (serviceSuccessInited) {
            okCallback()
            return
        }
        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.ERROR) {
                errorCallback()
            } else {
                serviceSuccessInited = true
                okCallback()
                val locale = userStorage.getCurrentReadingLanguage()
                locale?.let { textToSpeech.language = it }
            }
        })
        textToSpeech.setOnUtteranceProgressListener(object : EmptyUtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                next()
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                doWithHandler(textReadingError)
            }
        })
    }

    override fun setTextReadingListener(textChange: (ReadingPieceText) -> Unit,
                                        playCallback: () -> Unit,
                                        pauseCallback: () -> Unit,
                                        textReadingError: () -> Unit,
                                        endOfTextCallback: () -> Unit) {
        this.textChange = textChange
        this.playCallback = playCallback
        this.pauseCallback = pauseCallback
        this.textReadingError = textReadingError
        this.endOfTextCallback = endOfTextCallback

        initPageText()
        if (readingState == BookPresenter.READING_STATE.READING) {
            playCallback()
        } else {
            pauseCallback()
        }
    }

    override fun getAvailableLanguages(): List<Locale>? {
        return if (serviceSuccessInited) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.availableLanguages.toList()
            } else {
                Locale.getAvailableLocales().toList()
            }
        } else {
            null
        }
    }

    override fun changeReadingLanguage(locale: Locale) {
        userStorage.setCurrentReadingLanguage(locale)
        if (serviceSuccessInited) {
            textToSpeech.language = locale
        }
    }

    private fun hideNotificationIfOpened() {
        if (isNotificationShowed) {
            pause()
            notificationManager.cancel(NOTOFICATION_ID)
        }
    }

    override fun loadBook(bookId: Long, loadOkCallback: (BookDTO, ReadingText) -> Unit,
                          loadErrorCallback: () -> Unit,
                          bookEmptyErrorCallback: () -> Unit,
                          bookInfoCahngeCallback: (Int, Int) -> Unit) {
        this.bookInfoCahngeCallback = bookInfoCahngeCallback

        if (!this.bookId.equals(bookId)) {
            hideNotificationIfOpened()
            serviceSuccessInited = false
            this.bookId = bookId
            getBookListUseCase.execute(bookId, object : DefaultObserver<BookDTO>() {

                override fun onNext(response: BookDTO) {
                    bookDTO = response
                    speechRate = response.readingSpeed
                    pitchRate = response.readingPitch

                    readingText = ReadingData.readBook(bookDTO)

                    bookInfoCahngeCallback(speechRate, pitchRate)

                    if (isBookEmpty()) {
                        bookEmptyErrorCallback()
                    } else {
                        initStartPageAndSentence()
                        loadOkCallback(bookDTO, readingText)
                    }
                }

                override fun onError(e: Throwable) {
                    loadErrorCallback()
                }
            })
        } else {
            //book is already loaded
            readingText = ReadingData.readBook(bookDTO)
            bookInfoCahngeCallback(speechRate, pitchRate)
            initStartPageAndSentence()
            loadOkCallback(bookDTO, readingText)
        }
    }

    fun back() {
        val sentenceInPageIndex = readingPage.sentences.indexOf(readingSentence)
        if (sentenceInPageIndex.equals(0)) {
            //need to change page
            val pageInTextIndex = readingText.pages.indexOf(readingPage)
            if (pageInTextIndex.equals(0)) {
                //start of book
                pause()
            } else {
                //go to next page
                readingPage = readingText.pages[pageInTextIndex - 1]
                readingSentence = readingPage.sentences[readingPage.sentences.size - 1]
                initPageText()
                if (readingState == BookPresenter.READING_STATE.READING) speechCurrentSentence()
            }
        } else {
            //just next sentence
            readingSentence = readingPage.sentences[sentenceInPageIndex - 1]
            initPageText()
            if (readingState == BookPresenter.READING_STATE.READING) speechCurrentSentence()
        }
    }

    fun next() {
        val sentenceInPageIndex = readingPage.sentences.indexOf(readingSentence)
        if (sentenceInPageIndex.equals(readingPage.sentences.size - 1)) {
            //need to change page
            val pageInTextIndex = readingText.pages.indexOf(readingPage)
            if (pageInTextIndex.equals(readingText.pages.size - 1)) {
                //end of book
                pause()
                doWithHandler(endOfTextCallback)
            } else {
                //go to next page
                readingPage = readingText.pages[pageInTextIndex + 1]
                readingSentence = readingPage.sentences[0]
                initPageText()
                if (readingState == BookPresenter.READING_STATE.READING) speechCurrentSentence()
            }
        } else {
            //just next sentence
            readingSentence = readingPage.sentences[sentenceInPageIndex + 1]
            initPageText()
            if (readingState == BookPresenter.READING_STATE.READING) speechCurrentSentence()
        }
    }

    override fun moveToStartOfBook() {
        readingPage = readingText.pages[0]
        readingSentence = readingPage.sentences[0]
        initPageText()
        if (readingState == BookPresenter.READING_STATE.READING) pause()
    }

    private fun findPrevNextAndCurrentText(): ReadingPieceText {
        val currentSentenceIndex = readingPage.sentences.indexOf(readingSentence)
        val prevText = StringBuilder()
        for (i in 0..(currentSentenceIndex - 1)) {
            prevText.append(readingPage.sentences[i].text)
            prevText.append(" ")
        }
        val nextText = StringBuilder()
        for (i in (currentSentenceIndex + 1)..(readingPage.sentences.size - 1)) {
            nextText.append(readingPage.sentences[i].text)
            prevText.append(" ")
        }
        return ReadingPieceText(prevText.toString(), readingSentence, nextText.toString())
    }

    private fun initPageText() {
        handler.post { textChange(findPrevNextAndCurrentText()) }
        saveCurrentBookInfo()
    }

    private fun doWithHandler(doThat: () -> Unit) {
        handler.post { doThat() }
    }

    private fun isBookEmpty(): Boolean {
        if (readingText.pages.isEmpty() || readingText.pages[0].sentences.isEmpty() || readingText.pages[0].sentences[0].text.isEmpty()) {
            return true
        }
        return false
    }

    override fun currentPageSelected(page: ReadingPage) {
        if (readingState == BookPresenter.READING_STATE.READING) {
            pause()
            readingPage = page
            readingSentence = readingPage.sentences[0]
            initPageText()
            resume()
        } else {
            readingPage =  page
            readingSentence = readingPage.sentences[0]
            initPageText()
        }
    }

    private fun initStartPageAndSentence() {
        val readingProgress = Math.max(bookDTO.progress, 0)
        var sentenceNumber = 0
        readingPage = readingText.pages[0]
        readingSentence = readingPage.sentences[0]
        for (page in readingText.pages) {
            for (sentence in page.sentences) {
                if (sentenceNumber++.equals(readingProgress)) {
                    readingSentence = sentence
                    readingPage = page
                    break
                }
            }
        }
    }

    override fun resume() {
        readingState = BookPresenter.READING_STATE.READING
        doWithHandler(playCallback)
        updateNotification()
        speechCurrentSentence()
    }

    private fun speechCurrentSentence() {
        textToSpeech.setSpeechRate(speechRate / DIVIDE_TTS_SPEECH_RATE_BY)
        textToSpeech.setPitch(pitchRate / DIVIDE_TTS_PITCH_RATE_BY)
        textToSpeech.speak(readingSentence.text, TextToSpeech.QUEUE_FLUSH, speechParams)
    }

    override fun pause() {
        readingState = BookPresenter.READING_STATE.PAUSE
        doWithHandler(pauseCallback)
        if (isNotificationShowed) updateNotification()

        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
    }

    override fun prevSentence() {
        back()
    }

    override fun nextSentence() {
        next()
    }

    override fun changeBookInfo(speechRate: Int, pitchRate: Int) {
        this.speechRate = speechRate
        this.pitchRate = pitchRate
        doWithHandler({ bookInfoCahngeCallback(speechRate, pitchRate) })
    }

    override fun onDestroy() {
        hideNotificationIfOpened()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        hideNotificationIfOpened()
        stopSelf()
    }

    override fun stopIfPause() {
        if (readingState == BookPresenter.READING_STATE.PAUSE) {
            stopForeground(true)
            stopSelf()
            if (isNotificationShowed) {
                notificationManager.cancel(NOTOFICATION_ID)
            }
        }
    }

    override fun saveCurrentBookInfo() {
        val readingPosition = ReadingData.getSentenceIndexInText(readingText, readingSentence)
        bookDTO.progress = readingPosition
        updateBookInfoUseCase.execute(UpdateBookInfoUseCase.BookInfo(bookId, speechRate,
                pitchRate, readingPosition), object : DefaultObserver<Unit>() {
            override fun onNext(t: Unit) {

            }
        })
    }

    companion object {

        val DIVIDE_TTS_SPEECH_RATE_BY = 100.0f
        val DIVIDE_TTS_PITCH_RATE_BY = 100.0f

        val NOTOFICATION_ID = 774
        val NOTOFICATION_CHANNEL_ID = "999"
        val NOTOFICATION_CHANNEL_NAME = "book"
        val NOTOFICATION_CHANNEL_DESKR = "currentreadinbook"
    }
}