package com.pashkobohdan.ttsreader.mvp.bookList;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.pashkobohdan.ttsreader.model.dataExecutors.BookListDataExecutor;
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO;
import com.pashkobohdan.ttsreader.mvp.bookList.view.BookListView;
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action0;

@InjectViewState
public class BookListPresenter extends AbstractPresenter<BookListView> {

    @Inject
    BookListDataExecutor bookListDataExecutor;

    @Inject
    Context context;

    @Inject
    public BookListPresenter() {
    }

    @Override
    protected void onFirstViewAttach() {
        getViewState().showRefresh();
        bookListDataExecutor.execute(new Subscriber<List<BookDTO>>() {//TODO add EmptySubscriber !

            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(List<BookDTO> bookDTOs) {
                getViewState().hideRefresh();
                getViewState().showBookList(bookDTOs);
            }

            @Override
            public void onError(Throwable t) {
                getViewState().hideRefresh();
                getViewState().showDataExecutionError();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void saveBook(BookDTO bookDTO, Action0 successCallback, Action0 failureCallback) {
        getViewState().showRefresh();
        bookListDataExecutor.executeAddData(new Subscriber<Boolean>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                successCallback.call();
            }

            @Override
            public void onError(Throwable t) {
                failureCallback.call();
            }

            @Override
            public void onComplete() {
                getViewState().hideRefresh();
            }
        }, bookDTO);
    }
}
