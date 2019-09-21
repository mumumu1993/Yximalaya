package com.yuqingsen.yximalaya.presenters;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.yuqingsen.yximalaya.base.BaseApplication;
import com.yuqingsen.yximalaya.data.HistoryDao;
import com.yuqingsen.yximalaya.data.IHistoryDaoCallback;
import com.yuqingsen.yximalaya.interfaces.IHistoryCallback;
import com.yuqingsen.yximalaya.interfaces.IHistoryPresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private List<IHistoryCallback> mCallbacks = new ArrayList<>();

    private final HistoryDao mHistoryDao;

    private HistoryPresenter(){
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
    }
    private static HistoryPresenter sHistoryPresenter = null;
    public static HistoryPresenter getHistoryPresenter(){
        if (sHistoryPresenter == null) {
            synchronized (HistoryPresenter.class){
                if (sHistoryPresenter == null) {
                    sHistoryPresenter = new HistoryPresenter();
                }
            }
        }
        return sHistoryPresenter;
    }
    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void addHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        //UI注册过来的回调接口
        if (!mCallbacks.contains(iHistoryCallback)) {
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unregisterViewCallback(IHistoryCallback iHistoryCallback) {
            mCallbacks.remove(iHistoryCallback);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        listHistories();
    }

    @Override
    public void onHistoriesLoaded(final List<Track> tracks) {
        //通知UI更新数据
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback callback : mCallbacks) {
                    callback.onHistoryLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoriesClear(boolean isSuccess) {
        listHistories();
    }
}
