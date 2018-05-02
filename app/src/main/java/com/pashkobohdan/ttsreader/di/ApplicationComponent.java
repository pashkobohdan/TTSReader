package com.pashkobohdan.ttsreader.di;

import com.pashkobohdan.ttsreader.di.modules.AppModule;
import com.pashkobohdan.ttsreader.di.modules.DatabaseModule;
import com.pashkobohdan.ttsreader.di.modules.NavigationModule;
import com.pashkobohdan.ttsreader.ui.activities.MainActivity;
import com.pashkobohdan.ttsreader.ui.fragments.book.reading.BookFragment;
import com.pashkobohdan.ttsreader.ui.fragments.book.list.BookListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        AppModule.class,
        DatabaseModule.class,
        NavigationModule.class
})
@Singleton
public interface ApplicationComponent {

    void inject(MainActivity activity);

    void inject(BookListFragment fragment);

    void inject(BookFragment fragment);
}
