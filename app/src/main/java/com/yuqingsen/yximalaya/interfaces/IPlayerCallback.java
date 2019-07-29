package com.yuqingsen.yximalaya.interfaces;


import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback {
    /**
     * 开始播放
     */
    void onPlayStart();

    /**
     * 播放暂停
     */
    void onPlayPause();

    /**
     * 播放停止
     */
    void onplayStop();

    /**
     * 播放错误
     */
    void onPlayError();

    /**
     * 下一首播放
     */
    void onNextPlay(Track track);

    /**
     * 上一首播放
     */
    void onPrePlay(Track track);

    /**
     * 播放器列表数据加载完毕
     *
     * @param list 播放器列表数据
     */
    void onListLoade(List<Track> list);

    /**
     * 播放器模式改变
     *
     * @param playMode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 进度条的改变
     *
     * @param currentIndex
     * @param total
     */
    void onProgressChange(int currentIndex, int total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告结束
     */
    void onAdFinishede();

    /**
     * 更新当前节目
     * @param track
     */
    void onTrackUpdate(Track track,int playIndex);

    /**
     * 通知UI更新播放列表的文字和图标
     * @param isReverse
     */
    void updateListOrder(boolean isReverse);
}

