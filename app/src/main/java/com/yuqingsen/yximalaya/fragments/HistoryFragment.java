package com.yuqingsen.yximalaya.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.yuqingsen.yximalaya.R;
import com.yuqingsen.yximalaya.adapters.TrackListAdapter;
import com.yuqingsen.yximalaya.base.BaseApplication;
import com.yuqingsen.yximalaya.base.BaseFragment;
import com.yuqingsen.yximalaya.interfaces.IHistoryCallback;
import com.yuqingsen.yximalaya.presenters.HistoryPresenter;
import com.yuqingsen.yximalaya.views.UILoader;

import java.util.List;

public class HistoryFragment extends BaseFragment implements IHistoryCallback {

    private UILoader mUiLoader;
    private TrackListAdapter mTrackListAdapter;
    private HistoryPresenter mHistoryPresenter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_history,container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
        }else {
            if(mUiLoader.getParent() instanceof ViewGroup){
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            };
        }
        mHistoryPresenter = HistoryPresenter.getHistoryPresenter();
        mHistoryPresenter.registerViewCallback(this);
        rootView.addView(mUiLoader);
        return rootView;
    }

    private View createSuccessView(ViewGroup container) {
        View successView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_histories, container, false);
        RecyclerView historyList = successView.findViewById(R.id.history_list);
        historyList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mTrackListAdapter = new TrackListAdapter();
        historyList.setAdapter(mTrackListAdapter);
        return successView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onHistoryLoaded(List<Track> tracks) {
        mTrackListAdapter.setData(tracks);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }
}
