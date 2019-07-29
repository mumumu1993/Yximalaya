package com.yuqingsen.yximalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.yuqingsen.yximalaya.R;
import com.yuqingsen.yximalaya.adapters.PlayListAdapter;
import com.yuqingsen.yximalaya.base.BaseApplication;

import java.util.List;

public class SobPopWindow extends PopupWindow {

    private final View mPopView;
    private TextView mCloseBtn;
    private RecyclerView mTrackList;
    private PlayListAdapter mPlayListAdapter;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mPlayModeContainer;
    private PlayListActionClickListener mActionClickListener = null;
    private View mOrderBtnContainer;
    private ImageView mOrderIv;
    private TextView mOrderTv;

    public SobPopWindow() {
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        //加载View进来
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopView);
        //设置弹入弹出动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }

    private void initEvent() {
        //点击窗口消失
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionClickListener != null) {
                    mActionClickListener.onPlayModeClick();
                }
            }
        });
        mOrderBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionClickListener.onOrderClick();
            }
        });
    }

    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        //找到控件
        mTrackList = mPopView.findViewById(R.id.play_list_rv);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTrackList.setLayoutManager(layoutManager);
        //设置适配器
        mPlayListAdapter = new PlayListAdapter();
        mTrackList.setAdapter(mPlayListAdapter);
        //播放模式相关
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);
        //播放顺序相关
        mOrderIv = mPopView.findViewById(R.id.play_list_bylist_iv);
        mOrderTv = mPopView.findViewById(R.id.play_list_bylist_tv);
        mOrderBtnContainer = mPopView.findViewById(R.id.play_list_bylist_container);


    }

    //给适配器设置
    public void setListData(List<Track> data) {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setData(data);
        }
    }

    public void setCurrentPlayPosition(int position) {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setCurrentPlayPosition(position);
            mTrackList.scrollToPosition(position);
        }
    }

    public void setPlayListItemClickListener(PlayListItemClickListener listener) {
        mPlayListAdapter.setOnItemClickListener(listener);
    }

    /**
     * 更新播放列表的播放模式
     *
     * @param currentMode
     */
    public void updatePlayMode(XmPlayListControl.PlayMode currentMode) {
        updatePlayModeBtnImg(currentMode);
    }

    /**
     * 更新切换顺序播放和逆序播放的按钮和文字
     * @param isOrder
     */
    public void updateOrderIcon(boolean isOrder){
        mOrderIv.setImageResource(isOrder?R.drawable.selector_play_list_order :R.drawable.selector_play_list_reverse);
        mOrderTv.setText(BaseApplication.getAppContext().getResources().getString(isOrder?R.string.order_text:R.string.reverse_text));
    }
    /**
     * 根据当前的状态，更新播放模式图标
     */
    private void updatePlayModeBtnImg(XmPlayListControl.PlayMode playMode) {
        int resId = R.drawable.selector_play_mode_list;
        int textId = R.string.play_mode_order_text;
        switch (playMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_play_mode_random;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_play_mode_listloop;
                textId = R.string.play_mode_list_loop_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_play_mode_singleloop;
                textId = R.string.play_mode_single_loop_text;
                break;
        }
        mPlayModeIv.setImageResource(resId);
        mPlayModeTv.setText(textId);
    }

    public interface PlayListItemClickListener {
        void onItemClick(int position);
    }

    public void setPlayListActionClickListener(PlayListActionClickListener playListActionClickListener) {
        mActionClickListener = playListActionClickListener;
    }

    public interface PlayListActionClickListener {
        //播放模式被点击
        void onPlayModeClick();

        //播放顺序按钮被点击
        void onOrderClick();
    }
}
