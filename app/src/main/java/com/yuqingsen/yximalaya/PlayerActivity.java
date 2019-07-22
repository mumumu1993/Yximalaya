package com.yuqingsen.yximalaya;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.yuqingsen.yximalaya.adapters.PlayerTrackPagerAdapter;
import com.yuqingsen.yximalaya.base.BaseActivity;
import com.yuqingsen.yximalaya.interfaces.IPlayerCallback;
import com.yuqingsen.yximalaya.presenters.PlayerPresenter;
import com.yuqingsen.yximalaya.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.List;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mPlayerPresenter = PlayerPresenter.getsPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        initView();
        mPlayerPresenter.getPlayList();
        initEvent();
        startPlay();
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

    /**
     * 开始播放
     */
    private void startPlay() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.play();
        }
    }

    //给控件设置相关事件
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter.isPlay()){
                    mPlayerPresenter.pause();
                }else {
                    mPlayerPresenter.play();
                }
            }
        });
        //拖动进度条
        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
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
                switch (action){
                    case  MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                        break;
                }
                return false;
            }
        });
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
    }

    @Override
    public void onPlayStart() {
        //修改UI成暂停播放的按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.stop);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_circle);
        }
    }

    @Override
    public void onplayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_circle);
        };
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
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentIndex, int total) {
        mDurationBar.setMax(total);
        //更新播放进度
        String totalDuration;
        String currentPosition;
        if (total>1000*60*60) {
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentIndex);
        }else {
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentIndex);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }
        if (mCurrentPosition!=null){
            mCurrentPosition.setText(currentPosition);
        }
        if (!mIsUserTouchProgress) {
            LogUtil.d(TAG,"percent------->"+currentIndex);
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
        if (mTarckPagerView!=null){
            mTarckPagerView.setCurrentItem(playIndex,true);
        }
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
}
