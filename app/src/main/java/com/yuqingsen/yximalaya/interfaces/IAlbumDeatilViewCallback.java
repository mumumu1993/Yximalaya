package com.yuqingsen.yximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDeatilViewCallback {
    /**
     * 专辑详情内容加载出来了。
     * @param tracks
     */
    void  onDetailListLoaded(List<Track> tracks);

    void onNetworkError(int errorCode, String errorMsg);
    /**
     * 把album传给了UI
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 加载更多的结果
     * @param size size>0表示加载成功，否则表示加载失败
     */
    void onLoaderMoreFinished(int size);

    /**
     * 下拉刷新的结果
     * @param size size>0表示刷新成功，否则表示刷新失败；
     */
    void onRefreshFinished(int size);
}
