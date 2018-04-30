package com.pashkobohdan.ttsreader.model.dataExecutors.common;


import com.pashkobohdan.ttsreader.model.database.room.AppDatabase;
import com.pashkobohdan.ttsreader.model.dto.common.CommonDTO;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class CommonDataExecutor<T extends CommonDTO> {

    @Inject
    protected AppDatabase appDatabase;

    public Observable<List<T>> execute(rx.Subscriber<List<T>> subscriber) {
        Observable<List<T>> observable = createObservable();
        observable.subscribe(data -> {
            subscriber.onNext(data);
            subscriber.onCompleted();
        }, throwable -> {
            subscriber.onError(throwable);
            subscriber.onCompleted();
        });
        return observable;
    }

    public Observable executeAddData(rx.Subscriber<Boolean> subscriber, T data) {
        Observable<Boolean> observable = createAddDataObservable(data);
        observable.subscribe(result -> {
            subscriber.onNext(true);
            subscriber.onCompleted();
        }, throwable -> {
            subscriber.onError(throwable);
            subscriber.onCompleted();
        });
        return observable;
    }

    public Observable executeDeleteData(rx.Subscriber<Boolean> subscriber, T data) {
        Observable<Boolean> observable = createDeleteDataObservable(data);
        observable.subscribe(result -> {
            subscriber.onNext(true);
            subscriber.onCompleted();
        }, throwable -> {
            subscriber.onError(throwable);
            subscriber.onCompleted();
        });
        return observable;
    }

    public abstract List<T> getData() throws SQLException;

    public abstract Boolean addData(T data);

    public abstract Boolean deleteData(T data);

    private Observable<List<T>> createObservable() {
        return Observable
                .fromCallable(this::getData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Boolean> createAddDataObservable(T data) {
        return Observable
                .fromCallable(() -> addData(data))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread() );
    }

    private Observable<Boolean> createDeleteDataObservable(T data) {
        return Observable
                .fromCallable(() -> deleteData(data))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread() );
    }
}
