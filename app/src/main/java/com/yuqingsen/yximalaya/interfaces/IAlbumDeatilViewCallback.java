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
}
