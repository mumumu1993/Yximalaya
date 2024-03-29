package com.yuqingsen.yximalaya.presenters;

import android.support.annotation.Nullable;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.yuqingsen.yximalaya.data.YximalayaApi;
import com.yuqingsen.yximalaya.interfaces.IAlbumDeatilViewCallback;
import com.yuqingsen.yximalaya.interfaces.IAlbumDetailPresenter;
import com.yuqingsen.yximalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private List<IAlbumDeatilViewCallback> callbacks = new ArrayList<>();
    private Album mTargetAlbum = null;
    private long mCurrentAlumId = -1;
    private int mCurrentPageIndex = 0;

    private List<Track> mTracks = new ArrayList<>();

    //单例
    private AlbumDetailPresenter() {
    }

    private static AlbumDetailPresenter sInstance = null;

    public static AlbumDetailPresenter getsInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class) {
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {
        //加载更多内容
        mCurrentPageIndex++;
        doLoaded(true);
    }

    private void doLoaded(final boolean isLoaderMore){
        YximalayaApi yximalayaApi = YximalayaApi.getYximalayaApi();
        yximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG, "tracks size ------>" + tracks.size());
                    if (isLoaderMore) {
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handlerLoaderMoreResult(size);
                    }else {
                        mTracks.addAll(0,tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (isLoaderMore) {
                    mCurrentPageIndex--;
                }
                LogUtil.d(TAG, "errorCode----->" + errorCode);
                LogUtil.d(TAG, "errorMsg----->" + errorMsg);
                handlerError(errorCode, errorMsg);
            }
        },mCurrentAlumId,mCurrentPageIndex);
    }

    /**
     * 处理加载更多的结果
     * @param size
     */
    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDeatilViewCallback callback : callbacks) {
            callback.onLoaderMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetail(long albumID, int page) {
        mTracks.clear();
        this.mCurrentAlumId = albumID;
        this.mCurrentPageIndex = page;
       doLoaded(false);
    }

    /**
     * 如果发生错误就通知UI
     * @param errorCode
     * @param errorMsg
     */
    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbumDeatilViewCallback callback : callbacks) {
            callback.onNetworkError(errorCode,errorMsg);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDeatilViewCallback callback : callbacks) {
            callback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDeatilViewCallback deatilViewCallback) {
        if (!callbacks.contains(deatilViewCallback)) {
            callbacks.add(deatilViewCallback);
            if (mTargetAlbum != null) {
                deatilViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unregisterViewCallback(IAlbumDeatilViewCallback detailViewCallback) {
        callbacks.remove(detailViewCallback);
    }

    public void setTargetAlbum(Album targetAlbum) {
        this.mTargetAlbum = targetAlbum;
    }
}
