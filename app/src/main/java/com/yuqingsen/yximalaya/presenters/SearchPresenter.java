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
import com.yuqingsen.yximalaya.utils.Constants;
import com.yuqingsen.yximalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private List<Album> mSearchResult = new ArrayList<>();
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

    public static SearchPresenter getSearchPresenter(){
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
        mCurrentPage = DEFAULT_PAGE;
        mSearchResult.clear();
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
                mSearchResult.addAll(albums);
                if (albums!=null) {
                    LogUtil.d(TAG,"album size ------->"+albums.size());
                    if (mIsLoadMore){
                        for (ISearchCallback callback : mCallbacks) {
                            if (albums.size()==0) {
                                callback.onLoadMoreResult(mSearchResult,false);
                            }else {
                                callback.onLoadMoreResult(mSearchResult,true);
                            }

                        }
                        mIsLoadMore = false;
                    }else {
                        for (ISearchCallback callback : mCallbacks) {
                            callback.onSearchResultLoaded(mSearchResult);
                        }
                    }

                }else {
                    LogUtil.d(TAG,"albums is null--------------------");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG,"searchByKeyword  errorCode------->"+errorCode);
                LogUtil.d(TAG,"searchByKeyword  errorMsg------->"+errorMsg);

                    for (ISearchCallback callback : mCallbacks) {
                        if (mIsLoadMore) {
                            callback.onLoadMoreResult(mSearchResult,false);
                            mIsLoadMore = false;
                            mCurrentPage--;
                        }else {
                            callback.onError(errorCode,errorMsg);

                        }
                }

            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    private boolean mIsLoadMore = false;
    @Override
    public void loadMore() {
        if (mSearchResult.size()<Constants.COUNT_DEFAULT){
            for (ISearchCallback callback : mCallbacks) {
                callback.onLoadMoreResult(mSearchResult,false);
            }
        }else {
            mIsLoadMore =true;
            mCurrentPage++;
            search(mCurrentKeyword);
        }


    }

    @Override
    public void getHotWord() {
        mYximalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    LogUtil.d(TAG,"hotWords size -------->"+hotWords.size());
                    for (ISearchCallback callback : mCallbacks) {
                        callback.onHotWordLoaded(hotWords);
                    }
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
                    for (ISearchCallback callback : mCallbacks) {
                        callback.onRecommendWordLoaded(keyWordList);
                    }
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
