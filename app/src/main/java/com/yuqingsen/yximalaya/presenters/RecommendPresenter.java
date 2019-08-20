package com.yuqingsen.yximalaya.presenters;

import android.support.annotation.Nullable;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.yuqingsen.yximalaya.api.YximalayaApi;
import com.yuqingsen.yximalaya.interfaces.IRecommendPresenter;
import com.yuqingsen.yximalaya.interfaces.IRecommendViewCallback;
import com.yuqingsen.yximalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {

    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();
    private List<Album> mCurrentRecommend = null;

    private RecommendPresenter(){}
    private static RecommendPresenter sInstance = null ;
    private static final String TAG = "RecommendPresenter";
    /**
     * 获取单例对象
     * @return
     */
    public static RecommendPresenter getsInstance(){
        if (sInstance==null){
            synchronized (RecommendPresenter.class){
                if (sInstance==null){
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取当前的推荐专辑列表
     * @return 推荐专辑列表，使用前判空。
     */
    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }
    /**
     * 获取推荐内容
     * 实现接口：
     * 3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        updateLoading();
        YximalayaApi yximalayaApi = YximalayaApi.getYximalayaApi();
        yximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                //数据获取成功
                if (gussLikeAlbumList!=null){
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //拿到数据，更新UI
                    //upRecommendUI(albumList);
                    LogUtil.d(TAG,"————————>"+albumList.size());
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //获取数据失败
                LogUtil.d(TAG,"error -- >"+i);
                LogUtil.d(TAG,"errorMsg -- >"+s);
                handlerError();
            }
        });
    }

    private void handlerError() {
        if (mCallbacks!=null){
            for (IRecommendViewCallback callback:mCallbacks){
                callback.onNetworkError();
            }
        }
    }


    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks !=null && !mCallbacks.contains(callback)){
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks!=null){
            mCallbacks.remove(callback);
        }
    }



    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (albumList!=null){
            if (albumList.size()==0){
                for (IRecommendViewCallback callback:mCallbacks){
                    callback.onEmpty();
                }
            }else {
                for (IRecommendViewCallback callback:mCallbacks){
                    callback.onRecommendListLoaded(albumList);
                }
                this.mCurrentRecommend = albumList;
            }
        }
    }
    private void updateLoading(){
        for (IRecommendViewCallback callback:mCallbacks){
            callback.onLoading();
        }
    }
}
