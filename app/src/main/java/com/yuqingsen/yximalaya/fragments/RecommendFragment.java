package com.yuqingsen.yximalaya.fragments;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.yuqingsen.yximalaya.R;
import com.yuqingsen.yximalaya.adapters.RecommendListAdapter;
import com.yuqingsen.yximalaya.base.BaseFragment;
import com.yuqingsen.yximalaya.interfaces.IRecommendViewCallback;
import com.yuqingsen.yximalaya.presenters.RecommendPresenter;
import com.yuqingsen.yximalaya.utils.LogUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;


import java.util.List;


public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {
    private static final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView recommendList;
    private RecommendListAdapter recommendListAdapter;
    private RecommendPresenter recommendPresenter;
    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        rootView = layoutInflater.inflate(R.layout.fragment_recommend,container,false);
        recommendList = rootView.findViewById(R.id.recommend_list);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recommendList.setLayoutManager(linearLayoutManager);
        recommendList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
        //设置适配器
        recommendListAdapter = new RecommendListAdapter();
        recommendList.setAdapter(recommendListAdapter);

        //获取到逻辑层的对象
        recommendPresenter = RecommendPresenter.getsInstance();
        //先要设置接口注册通知
        recommendPresenter.registerViewCallback(this);
        //获取推荐列表
        recommendPresenter.getRecommendList();


        //返回view给界面显示
        return rootView;
    }


    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当我们获取到推荐内容的时候，这个方法就会被调用
        //数据到手，更新UI
        LogUtil.d(TAG,"------>"+result);
        recommendListAdapter.setData(result);
    }

    @Override
    public void onLoaderMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册
        if (recommendPresenter!=null){
            recommendPresenter.unRegisterViewCallback(this);
        }
    }
}
