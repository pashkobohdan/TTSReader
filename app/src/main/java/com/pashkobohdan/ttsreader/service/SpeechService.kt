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
import com.pashkobohdan.ttsreader.data.model.utils.ReadingText
import com.pashkobohdan.ttsreader.data.usecase.observers.DefaultObserver
import com.pashkobohdan.ttsreader.mvp.bookRead.BookPresenter
import com.pashkobohdan.ttsreader.ui.activities.MainActivity
import com.pashkobohdan.ttsreader.utils.Constants
import com.pashkobohdan.ttsreader.utils.TextSplitter
import com.pashkobohdan.ttsreader.utils.listeners.EmptyUtteranceProgressListener
import javax.inject.Inject

class SpeechService : Service(), TtsListener {

    @Inject
    lateinit var getBookListUseCase: GetBookByIdUseCase
    @Inject
    lateinit var updateBookInfoUseCase: UpdateBookInfoUseCase

    private lateinit var bookDTO: BookDTO
    private var bookId: Long = -1
    private var readingState: BookPresenter.READING_STATE = BookPresenter.READING_STATE.PAUSE
    private var readingPosition: Int = 0

    private lateinit var text: List<List<String>>
    private lateinit var currentPage: List<String>
    private lateinit var currentSentence: String

    private lateinit var textToSpeech: TextToSpeech
    private var textChange: (ReadingText) -> Unit = {}
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
                .setContentTitle("Title")
                .setContentText("Text")
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
            }
        })
        textToSpeech.setOnUtteranceProgressListener(object : EmptyUtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                next()
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                textReadingError()
            }
        })
    }

    override fun setTextReadingListener(textChange: (ReadingText) -> Unit,
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

    private fun hideNotificationIfOpened() {
        if (isNotificationShowed) {
            pause()
            notificationManager.cancel(NOTOFICATION_ID)
        }
    }

    override fun loadBook(bookId: Long, loadOkCallback: (BookDTO) -> Unit,
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
                    text = TextSplitter.readPages(bookDTO)

                    readingPosition = response.progress
                    speechRate = response.readingSpeed
                    pitchRate = response.readingPitch
                    bookInfoCahngeCallback(speechRate, pitchRate)

                    if (isBookEmpty()) {
                        bookEmptyErrorCallback()
                    } else {
                        initStartPageAndSentence()
                        loadOkCallback(bookDTO)
                    }
                }

                override fun onError(e: Throwable) {
                    loadErrorCallback()
                }
            })
        } else {
            //book is already loaded
            bookInfoCahngeCallback(speechRate, pitchRate)
            initStartPageAndSentence()
            loadOkCallback(bookDTO)
        }
    }

    fun back() {
        val sentenceInPageIndex = currentPage.indexOf(currentSentence)
        if (sentenceInPageIndex.equals(0)) {
            //need to change page
            val pageInTextIndex = text.indexOf(currentPage)
            if (pageInTextIndex.equals(0)) {
                //start of book
                pause()
            } else {
                //go to next page
                readingPosition--
                currentPage = text[pageInTextIndex - 1]
                currentSentence = currentPage[currentPage.size - 1]
                initPageText()
                if (readingState == BookPresenter.READING_STATE.READING) speechCurrentSentence()
            }
        } else {
            //just next sentence
            readingPosition--
            currentSentence = currentPage[sentenceInPageIndex - 1]
            initPageText()
            if (readingState == BookPresenter.READING_STATE.READING) speechCurrentSentence()
        }
    }

    fun next() {
        val sentenceInPageIndex = currentPage.indexOf(currentSentence)
        if (sentenceInPageIndex.equals(currentPage.size - 1)) {
            //need to change page
            val pageInTextIndex = text.indexOf(currentPage)
            if (pageInTextIndex.equals(text.size - 1)) {
                //end of book
                pause()
                endOfTextCallback()
            } else {
                //go to next page
                readingPosition++
                currentPage = text[pageInTextIndex + 1]
                currentSentence = currentPage[0]
                initPageText()
                if (readingState == BookPresenter.READING_STATE.READING) speechCurrentSentence()
            }
        } else {
            //just next sentence
            readingPosition++
            currentSentence = currentPage[sentenceInPageIndex + 1]
            initPageText()
            if (readingState == BookPresenter.READING_STATE.READING) speechCurrentSentence()
        }
    }

    private fun findPrevNextAndCurrentText(): ReadingText {
        val currentSentenceIndex = currentPage.indexOf(currentSentence)
        val prevText = StringBuilder()
        for (i in 0..(currentSentenceIndex - 1)) {
            prevText.append(currentPage[i])
        }
        val nextText = StringBuilder()
        for (i in (currentSentenceIndex + 1)..(currentPage.size - 1)) {
            nextText.append(currentPage[i])
        }
        return ReadingText(prevText.toString(), currentSentence, nextText.toString())
    }

    private fun initPageText() {
        handler.post { textChange(findPrevNextAndCurrentText()) }
        saveCurrentBookInfo()
    }

    private fun isBookEmpty(): Boolean {
        if (text.isEmpty() || text[0].isEmpty() || text[0][0].isEmpty()) {
            return true
        }
        return false
    }

    override fun currentPageSelected(page: Int) {
        if(readingState == BookPresenter.READING_STATE.READING) {
            pause()
            currentPage = text[page]
            readingPosition = TextSplitter.getProgressByCurrentPage(bookDTO, page)
            currentSentence = currentPage[0]
            initPageText()
            resume()
        } else {
            currentPage = text[page]
            readingPosition = TextSplitter.getProgressByCurrentPage(bookDTO, page)
            currentSentence = currentPage[0]
            initPageText()
        }
    }

    private fun initStartPageAndSentence() {
        val readingProgress = Math.max(bookDTO.progress, 0)
        var sentenceNumber = 0
        currentPage = text[0]
        currentSentence = currentPage[0]
        for (page in text) {
            for (sentence in page) {
                if (sentenceNumber++.equals(readingProgress)) {
                    currentSentence = sentence
                    currentPage = page
                    break
                }
            }
        }
    }

    override fun resume() {
        readingState = BookPresenter.READING_STATE.READING
        playCallback()
        updateNotification()
        speechCurrentSentence()
    }

    private fun speechCurrentSentence() {
        textToSpeech.setSpeechRate(speechRate / DIVIDE_TTS_SPEECH_RATE_BY)
        textToSpeech.setPitch(pitchRate / DIVIDE_TTS_PITCH_RATE_BY)
        textToSpeech.speak(currentSentence, TextToSpeech.QUEUE_FLUSH, speechParams)
    }

    override fun pause() {
        readingState = BookPresenter.READING_STATE.PAUSE
        pauseCallback()
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
        bookInfoCahngeCallback(speechRate, pitchRate)
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