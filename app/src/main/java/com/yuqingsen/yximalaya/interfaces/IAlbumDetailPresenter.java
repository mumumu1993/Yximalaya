package com.yuqingsen.yximalaya.interfaces;

import com.yuqingsen.yximalaya.base.IBasePresenter;

public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDeatilViewCallback> {
    /**
     * 下拉加载更多
     */
    void pull2RefreshMore();
    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     * @param albumID
     * @param page
     */
    void getAlbumDetail(long albumID,int page);
}
