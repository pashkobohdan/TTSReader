package com.pashkobohdan.ttsreader.model.dataExecutors;


import com.pashkobohdan.ttsreader.model.dataExecutors.common.CommonDataExecutor;
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

public class BookListDataExecutor extends CommonDataExecutor<BookDTO> {

    @Inject
    public BookListDataExecutor() {
        //For DI
    }

    @Override
    public List<BookDTO> getData() throws SQLException {
        return appDatabase.getBookDAO().getAllBookDtoList();
    }

    @Override
    public Boolean addData(BookDTO array) {
        appDatabase.getBookDAO().insertAllBookDTO(array);
        return true;//TODO fix later
    }
}
