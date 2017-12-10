package com.pashkobohdan.ttsreader.model.dataExecutors.common;


import com.pashkobohdan.ttsreader.model.database.room.AppDatabase;
import com.pashkobohdan.ttsreader.model.dto.common.CommonDTO;

import org.reactivestreams.Subscriber;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class CommonDataExecutor<T extends CommonDTO> {

    @Inject
    protected AppDatabase appDatabase;

    public Observable<List<T>> execute(Subscriber<List<T>> subscriber) {
        Observable<List<T>> observable = createObservable();
        observable.subscribe(data -> {
            subscriber.onNext(data);
            subscriber.onComplete();
        }, throwable -> {
            subscriber.onError(throwable);
            subscriber.onComplete();
        });
        return observable;
    }

    public Observable executeAddData(Subscriber<Boolean> subscriber, T data) {
        Observable<Boolean> observable = createAddDataObservable(data);
        observable.subscribe(result -> {
            subscriber.onNext(true);
            subscriber.onComplete();
        }, throwable -> {
            subscriber.onError(throwable);
            subscriber.onComplete();
        });
        return observable;
    }

    public abstract List<T> getData() throws SQLException;

    public abstract Boolean addData(T data);

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
}
