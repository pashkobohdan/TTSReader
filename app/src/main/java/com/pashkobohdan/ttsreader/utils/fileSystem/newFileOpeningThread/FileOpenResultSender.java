package com.pashkobohdan.ttsreader.utils.fileSystem.newFileOpeningThread;

import com.pashkobohdan.ttsreader.utils.fileSystem.newFileOpening.core.BookReadingResult;

/**
 * Created by Bohdan Pashko on 24.01.17.
 */
@FunctionalInterface
public interface FileOpenResultSender {

    void send(BookReadingResult file);

}
