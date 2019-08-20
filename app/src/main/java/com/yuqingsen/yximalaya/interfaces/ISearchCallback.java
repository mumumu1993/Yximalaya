package com.yuqingsen.yximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallback {

    /**
     * 搜索结果
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 获取推荐热词结果回调方法
     * @param hotWordList
     */
    void onHotWordLoaded(List<HotWord> hotWordList);

    /**
     * 加载更多结果返回
     * @param result 结果
     * @param isOkey true表示加载更多成功，false表示加载失败
     */
    void onLoadMoreResult(List<Album> result,boolean isOkey);

    /**
     * 关键字联想的结果
     * @param keyWordList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWordList);

    /**
     * 错误通知回调
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode,String errorMsg);
}
