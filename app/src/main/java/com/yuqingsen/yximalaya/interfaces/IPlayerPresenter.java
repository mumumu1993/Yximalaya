package com.yuqingsen.yximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.yuqingsen.yximalaya.base.IBasePresenter;

public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {

    void play();

    void pause();

    void stop();

    void playPre();

    void playNext();

    /**
     * 切换播放模式
     *
     * @param mode
     */
    void switchPlayMode(XmPlayListControl.PlayMode mode);

    /**
     * 获取播放列表
     */
    void getPlayList();

    /**
     * 根据进度条位置播放
     *
     * @param index
     */
    void playByIndex(int index);

    /**
     * 切换播放进度
     *
     * @param progress
     */
    void seekTo(int progress);

    /**
     * 播放器是否在播放
     *
     * @return
     */
    boolean isPlaying();

    /**
     * 把播放器列表内容反转
     */
    void reversPlayList();

    /**
     * 播放专辑的第一个节目
     * @param id
     */
    void playByAlbumId(long id);
}
