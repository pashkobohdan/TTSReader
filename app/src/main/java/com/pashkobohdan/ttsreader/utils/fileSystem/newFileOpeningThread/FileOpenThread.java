package com.pashkobohdan.ttsreader.utils.fileSystem.newFileOpeningThread;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.pashkobohdan.ttsreader.utils.fileSystem.file.core.PercentSender;
import com.pashkobohdan.ttsreader.utils.fileSystem.newFileOpening.AnyFileOpening;
import com.pashkobohdan.ttsreader.utils.fileSystem.newFileOpening.core.BookReadingResult;

import java.io.File;

/**
 * Opens file by thread.
 * Don't use runOnUiThread !- it's already implemented
 * <p>
 * Created by Bohdan Pashko on 23.01.17.
 */
public class FileOpenThread extends Thread {

    public FileOpenThread(@NonNull final File file, @NonNull final FragmentActivity activity,
                          @NonNull PercentSender readingPercentSender, @NonNull Runnable readingEnd,
                          @NonNull FileOpenResultSender fileOpeningEnd) {
        super(() -> {
            BookReadingResult outputFile = AnyFileOpening.open(file, activity,
                    (oldValue, neValue) -> activity.runOnUiThread(() -> readingPercentSender.refreshPercents(oldValue, neValue)),
                    () -> activity.runOnUiThread(readingEnd));

            activity.runOnUiThread(() -> fileOpeningEnd.send(outputFile));
        });
    }

}
