package com.pashkobohdan.ttsreader.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.pashkobohdan.ttsreader.R;
import com.pashkobohdan.ttsreader.TTSReaderProApplication;
import com.pashkobohdan.ttsreader.ui.common.CustomFragmentNavigator;
import com.pashkobohdan.ttsreader.ui.fragments.book.BookListFragment;

import javax.inject.Inject;

import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;
import ru.terrakok.cicerone.commands.Replace;

import static com.pashkobohdan.ttsreader.ui.Screen.BOOK_LIST;
import static com.pashkobohdan.ttsreader.ui.Screen.BOOK_READING;

public class MainActivity extends AppCompatActivity implements ToolbarHandler {
    private static final int CLICK_AGAIN_TO_EXIT_TIME = 2 * 1_000_000_000;

    @Inject
    NavigatorHolder navigatorHolder;
    @Inject
    Router router;

    private Navigator navigator = new CustomFragmentNavigator(getSupportFragmentManager(), R.id.main_container) {
        private long lastTryExitTime = 0L;

        @Override
        protected Fragment createFragment(String screenKey, Object data) {
            switch (screenKey) {
                case BOOK_LIST:
                    return BookListFragment.getNewInstance();
                case BOOK_READING:
                    //TODO
                default:
                    throw new IllegalArgumentException("Not supported screen: " + screenKey);
            }
        }

        @Override
        protected void showSystemMessage(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void exit() {
            long currentTime = System.nanoTime();
            if (currentTime - lastTryExitTime < CLICK_AGAIN_TO_EXIT_TIME) {
                finish();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.click_again_to_exit), Toast.LENGTH_SHORT).show();
                lastTryExitTime = currentTime;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        navigatorHolder.removeNavigator();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TTSReaderProApplication.INSTANCE.getApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            navigator.applyCommand(new Replace(BOOK_LIST, null));
        } else {
            navigator.applyCommand(new Replace(BOOK_LIST, null));
            //TODO maybe don't need this because I have a moxy (with stateStrategy)
        }
    }

    @Override
    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void showBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        router.exit();
    }
}
