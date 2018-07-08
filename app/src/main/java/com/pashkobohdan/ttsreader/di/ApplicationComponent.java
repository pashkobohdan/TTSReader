package com.pashkobohdan.ttsreader.di;

import com.pashkobohdan.ttsreader.di.modules.AppModule;
import com.pashkobohdan.ttsreader.di.modules.DatabaseModule;
import com.pashkobohdan.ttsreader.di.modules.NavigationModule;
import com.pashkobohdan.ttsreader.di.modules.SettingsModule;
import com.pashkobohdan.ttsreader.di.modules.UiModule;
import com.pashkobohdan.ttsreader.service.SpeechService;
import com.pashkobohdan.ttsreader.ui.activities.MainActivity;
import com.pashkobohdan.ttsreader.ui.fragments.book.list.BookListFragment;
import com.pashkobohdan.ttsreader.ui.fragments.book.reading.BookFragment;
import com.pashkobohdan.ttsreader.ui.fragments.cloudBook.CloudBookListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        AppModule.class,
        DatabaseModule.class,
        NavigationModule.class,
        SettingsModule.class,
        UiModule.class
})
@Singleton
public interface ApplicationComponent {

    void inject(MainActivity activity);

    void inject(BookListFragment fragment);

    void inject(BookFragment fragment);

    void inject(SpeechService service);

    void inject(CloudBookListFragment fragment);
}
