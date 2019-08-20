package com.yuqingsen.yximalaya.presenters;

import android.support.annotation.Nullable;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;
import com.yuqingsen.yximalaya.api.YximalayaApi;
import com.yuqingsen.yximalaya.interfaces.ISearchCallback;
import com.yuqingsen.yximalaya.interfaces.ISearchPresenter;
import com.yuqingsen.yximalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private static final String TAG = "SearchPresenter";
    //当前的搜索关键字
    private String mCurrentKeyword = null;
    private final YximalayaApi mYximalayaApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;

    private SearchPresenter(){
        mYximalayaApi = YximalayaApi.getYximalayaApi();
    }
    private static SearchPresenter sSearchPresenter = null;
    private static SearchPresenter getSearchPresenter(){
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class){
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    private List<ISearchCallback> mCallbacks = new ArrayList<>();
    @Override
    public void doSearch(String keyword) {
        //用于重新搜索
        //当网络不好的时候会显示重新搜索按钮
        this.mCurrentKeyword = keyword;
        search(keyword);
    }

    private void search(String keyword) {
        mYximalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {
                    LogUtil.d(TAG,"album size ------->"+albums.size());
                }else {
                    LogUtil.d(TAG,"albums is null--------------------");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG,"searchByKeyword  errorCode------->"+errorCode);
                LogUtil.d(TAG,"searchByKeyword  errorMsg------->"+errorMsg);
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {
        mYximalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    LogUtil.d(TAG,"hotWords size -------->"+hotWords.size());
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG,"getHotWords  errorCode------->"+errorCode);
                LogUtil.d(TAG,"getHotWords  errorMsg------->"+errorMsg);
            }
        });
    }

    @Override
    public void getRecommendWord(final String keyword) {
        mYximalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    LogUtil.d(TAG,"keyWordList size ------->"+keyWordList.size());
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG,"getSuggestWord  errorCode------->"+errorCode);
                LogUtil.d(TAG,"getSuggestWord  errorMsg------->"+errorMsg);
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallbacks.contains(iSearchCallback)) {
            mCallbacks.add(iSearchCallback);
        }
    }

    @Override
    public void unregisterViewCallback(ISearchCallback iSearchCallback) {
        mCallbacks.remove(iSearchCallback);
    }
}
