package com.pashkobohdan.ttsreader;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

public class MainActivity1 extends AppCompatActivity implements TextToSpeech.OnInitListener{
    @Override
    public void onInit(int status) {

    }

//    private Button mButton;
//    private TextToSpeech mTTS;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//
//        mTTS = new TextToSpeech(this, this);
////        mTTS.setPitch(3f);
//        mTTS.setSpeechRate(4f);
//
//        mButton = (Button) findViewById(R.id.tts_button);
//
//        mButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                String text = "Тульпы. Сообщество со странными идеями и целями, объединённое мистической терминологией. " +
//                        "Десятки людей, практикующих форсинг, гипноз, изменяющих своё сознание, медитирующих, пытающихся " +
//                        "справиться со своими заболеваниями. Все они собрались вместе из-за общей цели: создать то, что " +
//                        "Википедия называет «воображаемым другом». Но что такое тульпы на самом деле?\n" +
//                        "\n" +
//                        "Человечество пытается понять, как работает сознание, уже много веков, создавая и разрушая " +
//                        "сотни теорий. Возможно ли, что какие-то люди натурально предрасположены быть «множественными»," +
//                        " содержать несколько сознаний в своём разуме? Значит ли это, что остальные обречены жить в " +
//                        "одиночестве, «синглетами»?\n" +
//                        "\n" +
//                        "Я предлагаю вам мысленный эксперимент. Представьте, как вы ведёте машину на пути домой. " +
//                        "Вы приезжаете, открываете дверь, снимаете куртку, заглядываете в туалет, а потом идёте на кухню " +
//                        "и готовите себе бутерброд. Внезапно вы замечаете, что не помните, куда положили ключи от машины, " +
//                        "и возвращаетесь по своим шагам через весь дом, пытаясь их найти. Звучит знакомо? " +
//                        "Вы были живы всё это время, скорее всего — в сознании. Но вы сделали что-то, не отдавая себе " +
//                        "отчёта о происходящем.\n" +
//                        "\n" +
//                        "Вот ещё пример. Вы выходите из дома, чтобы купить молоко и яйца в близлежащем магазине. " +
//                        "Закрывая дверь, вы думаете об омлете, который планируете приготовить. Вы прогуливаетесь " +
//                        "по улице, и мысли перепрыгивают к чему-то ещё: к покупкам, которые надо сделать, к списку задач " +
//                        "на завтра, к игре, которую вы смотрели вчера. Вы замечаете, что уже пришли к магазину, и мысли " +
//                        "снова фокусируются на молоке и яйцах, но когда пытаетесь вспомнить, о чём думали, пока шли в " +
//                        "магазин, в голову не приходит ничего конкретного, а только общая идея о том, что вы должны были " +
//                        "о чём-то думать.\n" +
//                        "\n" +
//                        "Был ли прошлый «вы» тем же, как и тот, который пошёл в магазин?\n" +
//                        "\n" +
//                        "Некоторые психологи считают сознание прерываемым, а не процессом, который начинается от рождения " +
//                        "и идёт до смерти. С этим просто согласиться — ведь мы не в сознании, когда спим. И действительно, есть десятки других причин, почему вы можете временно находиться без сознания.";
//                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onInit(int status) {
//        // TODO Auto-generated method stub
//        if (status == TextToSpeech.SUCCESS) {
//
//            Locale locale = new Locale("ru");
//
////            int result = mTTS.setLanguage(locale);
////            //int result = mTTS.setLanguage(Locale.getDefault());
////
////            if (result == TextToSpeech.LANG_MISSING_DATA
////                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
////                Log.e("TTS", "Извините, этот язык не поддерживается");
////            } else {
////                mButton.setEnabled(true);
////            }
//
//        } else {
//            Log.e("TTS", "Ошибка!");
//        }
//    }
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        //TODO ! pauseMode TTS
//    }
//
//    @Override
//    public void onDestroy() {
//        Toast.makeText(MainActivity1.this, "STOP !", Toast.LENGTH_SHORT).show();
//        // Don't forget to shutdown mTTS!
//        if (mTTS != null) {
//            mTTS.stop();
//            mTTS.shutdown();
//        }
//        super.onDestroy();
//    }
}
