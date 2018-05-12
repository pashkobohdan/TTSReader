package com.pashkobohdan.ttsreader.data.model.dto.file;


import com.pashkobohdan.ttsreader.data.model.dto.common.CommonDTO;

import java.io.File;

public class FileDTO extends CommonDTO {

    private File file;

    public FileDTO(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean getIsDirectory() {
        return file == null || file.isDirectory();
    }
}
