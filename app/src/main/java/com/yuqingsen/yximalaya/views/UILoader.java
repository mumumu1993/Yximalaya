package com.yuqingsen.yximalaya.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yuqingsen.yximalaya.R;
import com.yuqingsen.yximalaya.base.BaseApplication;

public abstract class UILoader extends FrameLayout {
    private  OnRetryClickListener onRetryClickListener = null;
    private View loadingView;
    private View successView;
    private View networkErrorView;
    private View enptyView;

    public enum UIStatus{
        LOADING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }
    public UIStatus mCurrentStatus = UIStatus.NONE;
    public  UILoader(@NonNull Context context) {
        this(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void updateStatus(UIStatus status){
        mCurrentStatus = status;
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }
    /**
     * 初始化UI
     */
    private void init() {
        switchUIByCurrentStatus();
    }

    private void switchUIByCurrentStatus() {
        //加载中
        if (loadingView == null) {
            loadingView = getLoadingView();
            addView(loadingView);
        }
        //根据状态设置是否可见
        loadingView.setVisibility(mCurrentStatus == UIStatus.LOADING?VISIBLE:GONE);
        //成功
        if (successView == null) {
            successView = getSuccessView(this);
            addView(successView);
        }
        successView.setVisibility(mCurrentStatus == UIStatus.SUCCESS?VISIBLE:GONE);
        //网络错误界面
        if (networkErrorView == null) {
            networkErrorView = getNetworkErrorView();
            addView(networkErrorView);
        }
        networkErrorView.setVisibility(mCurrentStatus == UIStatus.NETWORK_ERROR?VISIBLE:GONE);
        //数据为空的界面
        if (enptyView == null) {
            enptyView = getEnptyView();
            addView(enptyView);
        }
        enptyView.setVisibility(mCurrentStatus == UIStatus.EMPTY?VISIBLE:GONE);
    }

    private View getEnptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_enpty_view,this,false);
    }

    private View getNetworkErrorView() {
        View networkErrorView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_error_view,this,false);
        networkErrorView.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //去重新获取数据刷新界面
                if (onRetryClickListener != null) {
                    onRetryClickListener.onRetryClick();
                }
            }
        });
        return networkErrorView;
    }

    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view,this,false);
    }

    public void setOnRetryClickListener(OnRetryClickListener listener){
        this.onRetryClickListener = listener;
    }

    public interface OnRetryClickListener{
        void onRetryClick( );
    }
}
