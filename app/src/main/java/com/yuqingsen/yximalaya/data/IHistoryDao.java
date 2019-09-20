package com.yuqingsen.yximalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryDao {
    /**
     * 设置回调接口
     * @param callback
     */
    void setCallback(IHistoryDaoCallback callback);

    void addHistory(Track track);

    void delHistory(Track track);

    void clearHistory();

    void listHistories();
}
