package com.yuqingsen.yximalaya;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.yuqingsen.yximalaya.adapters.PlayerTrackPagerAdapter;
import com.yuqingsen.yximalaya.base.BaseActivity;
import com.yuqingsen.yximalaya.interfaces.IPlayerCallback;
import com.yuqingsen.yximalaya.presenters.PlayerPresenter;
import com.yuqingsen.yximalaya.utils.LogUtil;
import com.yuqingsen.yximalaya.views.SobPopWindow;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {
    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgress = false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreviousBtn;
    private TextView mTrackTitleTv;
    private String mTrackTitleText;
    private ViewPager mTarckPagerView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsUserSlidePager = false;
    private ImageView mPlayModeSwitchBtn;

    private XmPlayListControl.PlayMode mCurrentMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeMap = new HashMap<>();

    //1、默认是列表播放：PLAY_MODEL_LIST
    //2、列表循环：      PLAY_MODEL_LIST_LOOP
    //3、单曲循环：      PLAY_MODEL_SINGLE_LOOP
    //4、随机播放：      PLAY_MODEL_RANDOM
    static {
        sPlayModeMap.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModeMap.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_SINGLE_LOOP);
        sPlayModeMap.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_RANDOM);
        sPlayModeMap.put(PLAY_MODEL_RANDOM, PLAY_MODEL_LIST);
    }

    private ImageView mPlayListFillBtn;
    private SobPopWindow mSobPopWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimator;
    public final int BG_ANIMATION_DURATION = 500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        mPlayerPresenter = PlayerPresenter.getsPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        mPlayerPresenter.getPlayList();
        initEvent();
        initBgAnimation();
    }

    private void initBgAnimation() {
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f,0.7f);
        mEnterBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                updateBgAlpha(value);
            }
        });
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f,1.0f);
        mOutBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                updateBgAlpha(value);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unregisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }


    //给控件设置相关事件
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter.isPlay()) {
                    mPlayerPresenter.pause();
                } else {
                    mPlayerPresenter.play();
                }
            }
        });
        //拖动进度条
        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgress = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgress = false;
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });

        mPlayPreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放前一个
                mPlayerPresenter.playPre();
            }
        });
        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放下一个
                mPlayerPresenter.playNext();
            }
        });

        mTarckPagerView.addOnPageChangeListener(this);

        mTarckPagerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                        break;
                }
                return false;
            }
        });

        mPlayModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swichPlayMode();
            }
        });
        mPlayListFillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示播放列表
                mSobPopWindow.showAtLocation(v,Gravity.BOTTOM,0,0);
                //处理背景
                mEnterBgAnimator.start();
            }
        });
        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mOutBgAnimator.start();
            }
        });
        mSobPopWindow.setPlayListItemClickListener(new SobPopWindow.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //播放里的Item被点击了
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                    mSobPopWindow.dismiss();
                }
            }
        });
        mSobPopWindow.setPlayListActionClickListener(new SobPopWindow.PlayListActionClickListener() {
            @Override
            public void onPlayModeClick() {
                swichPlayMode();
            }

            @Override
            public void onOrderClick() {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversPlayList();
                }
            }
        });
    }
    private boolean isOrder = false;
    private void swichPlayMode() {
        XmPlayListControl.PlayMode playMode = sPlayModeMap.get(mCurrentMode);
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
    }

    public void updateBgAlpha(float alpha){
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }
    /**
     * 根据当前的状态，更新播放模式图标
     */
    private void updatePlayModeBtnImg() {
        int resId = R.drawable.selector_play_mode_list;
        switch (mCurrentMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_play_mode_random;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_play_mode_listloop;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_play_mode_singleloop;
                break;
        }
        mPlayModeSwitchBtn.setImageResource(resId);
    }

    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mDurationBar = this.findViewById(R.id.track_seek_bar);
        mPlayNextBtn = this.findViewById(R.id.play_next_iv);
        mPlayPreviousBtn = this.findViewById(R.id.play_previous_iv);
        mTrackTitleTv = this.findViewById(R.id.track_title);
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitleTv.setText(mTrackTitleText);
        }
        mTarckPagerView = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        //设置适配器
        mTarckPagerView.setAdapter(mTrackPagerAdapter);
        mPlayModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);
        mPlayListFillBtn = this.findViewById(R.id.play_list_fill_iv);
        mSobPopWindow = new SobPopWindow();
    }

    @Override
    public void onPlayStart() {
        //修改UI成暂停播放的按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onplayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
        ;
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoade(List<Track> list) {
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
        //数据回来后也要给数据列表一份
        if (mSobPopWindow != null) {
            mSobPopWindow.setListData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        mCurrentMode = playMode;
        mSobPopWindow.updatePlayMode(mCurrentMode);
        updatePlayModeBtnImg();
    }

    @Override
    public void onProgressChange(int currentIndex, int total) {
        mDurationBar.setMax(total);
        //更新播放进度
        String totalDuration;
        String currentPosition;
        if (total > 1000 * 60 * 60) {
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentIndex);
        } else {
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentIndex);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        if (!mIsUserTouchProgress) {
            LogUtil.d(TAG, "percent------->" + currentIndex);
            mDurationBar.setProgress(currentIndex);
        }

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinishede() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitleTv != null) {
            mTrackTitleTv.setText(mTrackTitleText);
        }
        if (mTarckPagerView != null) {
            mTarckPagerView.setCurrentItem(playIndex, true);
        }
        //设置播放列表当前播放的位置
        if (mSobPopWindow != null) {
            mSobPopWindow.setCurrentPlayPosition(playIndex);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {
        mSobPopWindow.updateOrderIcon(!isReverse);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (mPlayerPresenter != null && mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(i);
        }
        mIsUserSlidePager = false;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
