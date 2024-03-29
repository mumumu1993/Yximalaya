package com.yuqingsen.yximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionCallback {
    /**
     * 调用添加，通知UI结果
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除订阅的回调方法
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 订阅专辑加载的结果回调
     * @param albums
     */
    void onSubscriptionsLoaded(List<Album> albums);

    /**
     * 订阅数已经满了
     */
    void onSubFull();
}
