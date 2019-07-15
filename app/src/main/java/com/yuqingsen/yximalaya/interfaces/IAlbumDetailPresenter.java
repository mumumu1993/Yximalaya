package com.yuqingsen.yximalaya.interfaces;

public interface IAlbumDetailPresenter {
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

    /**
     * 注册UI通知接口
     * @param deatilViewCallback
     */
    void registerViewCallback(IAlbumDeatilViewCallback deatilViewCallback);

    /**
     * 注销UI通知接口
     * @param deatilViewCallback
     */
    void unregisterViewCallback(IAlbumDeatilViewCallback deatilViewCallback);
}
