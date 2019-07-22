package com.yuqingsen.yximalaya.base;



public interface IBasePresenter<T> {
    /**
     * 注册UI通知接口
     */
    void registerViewCallback(T t);

    /**
     * 注销UI通知接口
     */
    void unregisterViewCallback(T t);
}
