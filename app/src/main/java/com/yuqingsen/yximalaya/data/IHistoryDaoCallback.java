package com.yuqingsen.yximalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryDaoCallback {

    void onHistoryAdd(boolean isSuccess);

    void onHistoryDel(boolean isSuccess);

    void onHistoriesLoaded(List<Track> tracks);

    void onHistoriesClear(boolean isSuccess);
}
