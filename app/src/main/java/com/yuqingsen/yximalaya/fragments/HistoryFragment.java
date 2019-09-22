package com.yuqingsen.yximalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.yuqingsen.yximalaya.PlayerActivity;
import com.yuqingsen.yximalaya.R;
import com.yuqingsen.yximalaya.adapters.TrackListAdapter;
import com.yuqingsen.yximalaya.base.BaseApplication;
import com.yuqingsen.yximalaya.base.BaseFragment;
import com.yuqingsen.yximalaya.interfaces.IHistoryCallback;
import com.yuqingsen.yximalaya.presenters.HistoryPresenter;
import com.yuqingsen.yximalaya.presenters.PlayerPresenter;
import com.yuqingsen.yximalaya.views.ConfirmCheckBoxDialog;
import com.yuqingsen.yximalaya.views.UILoader;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class HistoryFragment extends BaseFragment implements IHistoryCallback, TrackListAdapter.ItemClickListener, TrackListAdapter.ItemLongPressListener, ConfirmCheckBoxDialog.OnDialogActionClickListener {

    private UILoader mUiLoader;
    private TrackListAdapter mTrackListAdapter;
    private HistoryPresenter mHistoryPresenter;
    private Track mCurrentClickHistoryItem =null;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_history,container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                @Override
                protected View getEmptyView() {
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView emptyText = emptyView.findViewById(R.id.empty_view_tips_tv);
                    emptyText.setText("暂无历史记录");
                    return emptyView;
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
        TwinklingRefreshLayout refreshLayout = successView.findViewById(R.id.history_over_scroll_view);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableOverScroll(true);
        RecyclerView historyList = successView.findViewById(R.id.history_list);
        historyList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        historyList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        mTrackListAdapter = new TrackListAdapter();
        mTrackListAdapter.setItemClickListener(this);
        mTrackListAdapter.setItemLongPressListener(this);
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
        if (tracks==null||tracks.size()==0){
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        }else {
            mTrackListAdapter.setData(tracks);
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }

    }

    @Override
    public void onItemClick(List<Track> detailData, int i) {
        PlayerPresenter playerPresenter = PlayerPresenter.getsPlayerPresenter();
        playerPresenter.setPlayList(detailData, i);

        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongPress(Track track, int i) {
        this.mCurrentClickHistoryItem = track;
        //删除历史
        ConfirmCheckBoxDialog confirmCheckBoxDialog = new ConfirmCheckBoxDialog(getActivity());
        confirmCheckBoxDialog.setOnDialogActionClickListener(this);
        confirmCheckBoxDialog.show();
    }

    @Override
    public void onCancelClick() {
        //不用做
    }

    @Override
    public void onConfirmClick(boolean isCheck) {
        //去删除历史
        if (mHistoryPresenter!=null&&mCurrentClickHistoryItem != null) {
            if (!isCheck) {
                mHistoryPresenter.delHistory(mCurrentClickHistoryItem);
            }else {
                mHistoryPresenter.cleanHistory();
            }
        }
    }
}
