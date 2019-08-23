package com.yuqingsen.yximalaya.presenters;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.yuqingsen.yximalaya.base.BaseApplication;
import com.yuqingsen.yximalaya.data.ISubDaoCallback;
import com.yuqingsen.yximalaya.data.SubscriptionDao;
import com.yuqingsen.yximalaya.interfaces.ISubscriptionCallback;
import com.yuqingsen.yximalaya.interfaces.ISubscriptionPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {

    private final SubscriptionDao mSubscriptionDao;
    private Map<Long,Album> mData = new HashMap<>();
    private List<ISubscriptionCallback> mCallbacks = new ArrayList<>();

    private SubscriptionPresenter(){
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallback(this);
        listSubscriptions();
    };
    private void listSubscriptions(){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                //只调用，不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.listAlbum();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }
    private static SubscriptionPresenter sInstance = null;
    public static SubscriptionPresenter getInstance(){
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class){
                if (sInstance == null){
                    sInstance = new SubscriptionPresenter();
                }
            }
        }
        return sInstance;
    }
    @Override
    public void addSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.addAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.delAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscriptionList() {
        listSubscriptions();
    }

    @Override
    public boolean isSub(Album album) {
        Album result = mData.get(album.getId());
        return result == null;
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (mCallbacks.contains(iSubscriptionCallback)) {
            mCallbacks.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unregisterViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        mCallbacks.remove(iSubscriptionCallback);
    }

    @Override
    public void onAddResult(final boolean isSuccess) {
        //添加结果的回调
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final boolean isSuccess) {
        //删除订阅结果的回调
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(final List<Album> result) {
        //加载数据的回调
        for (Album album : result) {
            mData.put(album.getId(),album);
        }
        // 通知UI更新
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onSubscriptionsLoaded(result);
                }
            }
        });
    }
}
