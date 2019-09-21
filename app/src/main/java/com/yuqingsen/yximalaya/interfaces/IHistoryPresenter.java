package com.yuqingsen.yximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.yuqingsen.yximalaya.base.IBasePresenter;

public interface IHistoryPresenter extends IBasePresenter<IHistoryCallback> {

    void listHistories();

    void addHistory(Track track);

    void delHistory(Track track);

    void cleanHistory(Track track);


}
