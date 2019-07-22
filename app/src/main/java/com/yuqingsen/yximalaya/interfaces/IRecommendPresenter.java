package com.yuqingsen.yximalaya.interfaces;

import com.yuqingsen.yximalaya.base.IBasePresenter;

public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback> {
    /**
     * 获取推荐内容
     */
    void getRecommendList();
}
