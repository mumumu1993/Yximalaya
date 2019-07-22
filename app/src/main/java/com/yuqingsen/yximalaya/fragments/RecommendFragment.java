package com.yuqingsen.yximalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.yuqingsen.yximalaya.DetailActivity;
import com.yuqingsen.yximalaya.R;
import com.yuqingsen.yximalaya.adapters.RecommendListAdapter;
import com.yuqingsen.yximalaya.base.BaseFragment;
import com.yuqingsen.yximalaya.interfaces.IRecommendViewCallback;
import com.yuqingsen.yximalaya.presenters.AlbumDetailPresenter;
import com.yuqingsen.yximalaya.presenters.RecommendPresenter;
import com.yuqingsen.yximalaya.utils.LogUtil;
import com.yuqingsen.yximalaya.views.UILoader;

import net.lucode.hackware.magicindicator.buildins.UIUtil;


import java.util.List;


public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, RecommendListAdapter.OnRecommendItemClickListener {
    private static final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView recommendList;
    private RecommendListAdapter recommendListAdapter;
    private RecommendPresenter recommendPresenter;
    private UILoader uiLoader;
    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {

        uiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater,container);
            }
        };

        //获取到逻辑层的对象
        recommendPresenter = RecommendPresenter.getsInstance();
        //先要设置接口注册通知
        recommendPresenter.registerViewCallback(this);
        //获取推荐列表
        recommendPresenter.getRecommendList();

        if (uiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) uiLoader.getParent()).removeView(uiLoader);
        }

        uiLoader.setOnRetryClickListener(this);
        //返回view给界面显示
        return uiLoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
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
        recommendListAdapter.setOnRecommendItemClickListener(this);
        return rootView;
    }


    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当我们获取到推荐内容的时候，这个方法就会被调用
        //数据到手，更新UI
        LogUtil.d(TAG,"------>"+result);
        recommendListAdapter.setData(result);
        uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        uiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        uiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        uiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册
        if (recommendPresenter!=null){
            recommendPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //表示网络不佳，用户点击重试
        if (recommendPresenter != null) {
            recommendPresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getsInstance().setTargetAlbum(album);
        //item被点击了,跳转到详情界面
        Intent intent = new Intent(getContext(),DetailActivity.class);
        startActivity(intent);
    }
}
