package com.pashkobohdan.ttsreader.utils.fileSystem.newFileOpening.implementations;

import android.support.annotation.NonNull;

import com.pashkobohdan.ttsreader.utils.fileSystem.file.FileReadingAndWriting;
import com.pashkobohdan.ttsreader.utils.fileSystem.file.InternalStorageFileHelper;
import com.pashkobohdan.ttsreader.utils.fileSystem.file.core.FileReadWrite;
import com.pashkobohdan.ttsreader.utils.fileSystem.file.core.PercentSender;
import com.pashkobohdan.ttsreader.utils.fileSystem.newFileOpening.core.BookReadingResult;
import com.pashkobohdan.ttsreader.utils.fileSystem.newFileOpening.core.FileOpen;

import java.io.File;

/**
 * Opening TXT files
 *
 * Created by Bohdan Pashko on 16.01.17.
 */

public class TxtFileOpener implements FileOpen {


    @Override
    public BookReadingResult open(@NonNull File file, @NonNull PercentSender readingPercentSender, @NonNull Runnable readingEndSender) {

        FileReadWrite fileReadWrite = new FileReadingAndWriting();


        String fileText = fileReadWrite.read(file, readingPercentSender);

        fileText = fileText.trim();                         // delete first and last space (if exist)
        fileText = fileText.replaceAll("\\s+", " ");        // delete all duplicate white spaces
        fileText = fileText.replaceAll("(\\.)+", "\\.");        // delete all duplicate dots

        if(fileText == null){
            return null;
        }


        readingEndSender.run();
        return new BookReadingResult(fileText, InternalStorageFileHelper.fileNameWithoutExtension(file), "");

    }
}
