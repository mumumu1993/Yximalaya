package com.yuqingsen.yximalaya.fragments;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.yuqingsen.yximalaya.R;
import com.yuqingsen.yximalaya.adapters.RecommendListAdapter;
import com.yuqingsen.yximalaya.base.BaseFragment;
import com.yuqingsen.yximalaya.utils.Constants;
import com.yuqingsen.yximalaya.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment {
    private static final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView recommendList;
    private RecommendListAdapter recommendListAdapter;
    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        rootView = layoutInflater.inflate(R.layout.fragment_recommend,container,false);
        recommendList = rootView.findViewById(R.id.recommend_list);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recommendList.setLayoutManager(linearLayoutManager);
        //设置适配器
        recommendListAdapter = new RecommendListAdapter();
        recommendList.setAdapter(recommendListAdapter);

        //拿数据回来
        getRecommendData();


        //返回view给界面显示
        return rootView;
    }

    /**
     * 获取推荐内容
     * 实现接口：
     * 3.10.6 获取猜你喜欢专辑
     */
    private void getRecommendData() {
        //封装参数
        Map<String, String> map = new HashMap<>();
        //一页数据返回条数
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT+"");

        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                //数据获取成功
                if (gussLikeAlbumList!=null){
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //拿到数据，更新UI
                    upRecommendUI(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //获取数据失败
                LogUtil.d(TAG,"error -- >"+i);
                LogUtil.d(TAG,"errorMsg -- >"+s);
            }
        });
    }

    private void upRecommendUI(List<Album> albumList) {
        //把数据设置给适配器并更新
        recommendListAdapter.setData(albumList);
    }
}
