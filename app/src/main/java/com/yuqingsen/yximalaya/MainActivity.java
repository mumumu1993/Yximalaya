package com.yuqingsen.yximalaya;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.yuqingsen.yximalaya.adapters.IndicatorAdapter;
import com.yuqingsen.yximalaya.adapters.MainContentAdapter;
import com.yuqingsen.yximalaya.interfaces.IPlayerCallback;
import com.yuqingsen.yximalaya.presenters.PlayerPresenter;
import com.yuqingsen.yximalaya.utils.LogUtil;
import com.yuqingsen.yximalaya.views.RoundRectImageView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;


public class MainActivity extends FragmentActivity implements IPlayerCallback {

    private String TAG = "MainActivity";
    public MagicIndicator magicIndicator;
    public ViewPager contentPager;
    public IndicatorAdapter indicatorAdapter;
    private RoundRectImageView mRoundRectImageView;
    private TextView mCoverTitle;
    private TextView mCoverAuthor;
    private ImageView mCoverPlayControl;
    private PlayerPresenter mPlayerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        initPresenters();
    }

    private void initPresenters() {
        mPlayerPresenter = PlayerPresenter.getsPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
    }

    private void initEvent() {
        indicatorAdapter.setOnIndicatorTabClickListenr(new IndicatorAdapter.OnIndicatorTabClickListener() {
            @Override
            public void onTabClick(int i) {
                LogUtil.d(TAG,"click index is"+i);
                if (contentPager!=null){
                    contentPager.setCurrentItem(i);
                }
            }
        });
        mCoverPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    if (mPlayerPresenter.isPlaying()) {
                        mPlayerPresenter.pause();
                    }else {
                        mPlayerPresenter.play();
                    }
                }
            }
        });
    }

    private void initView() {
        magicIndicator = findViewById(R.id.main_indicator);
        magicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建适配器
        indicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(indicatorAdapter);


        //获取ViewPager
        contentPager = this.findViewById(R.id.content_pager);

        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);

        contentPager.setAdapter(mainContentAdapter);

        //绑定
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator,contentPager);

        //底部播放控制相关
        mRoundRectImageView = this.findViewById(R.id.track_main_cover);
        mCoverTitle = this.findViewById(R.id.track_main_cover_title);
        mCoverTitle.setSelected(true);
        mCoverAuthor = this.findViewById(R.id.track_main_cover_author);
        mCoverPlayControl = this.findViewById(R.id.track_main_cover_play_control);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter!=null){
            mPlayerPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onPlayStart() {
        if (mCoverPlayControl != null) {
            mCoverPlayControl.setImageResource(R.drawable.selector_main_pause);
        }
    }

    @Override
    public void onPlayPause() {
        if (mCoverPlayControl != null) {
            mCoverPlayControl.setImageResource(R.drawable.selector_main_play);
        }
    }

    @Override
    public void onplayStop() {
        if (mCoverPlayControl != null) {
            mCoverPlayControl.setImageResource(R.drawable.selector_main_play);
        }
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

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentIndex, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinishede() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            if (mCoverTitle != null) {
                mCoverTitle.setText(trackTitle);
            }
            if (mCoverAuthor != null) {
                mCoverAuthor.setText(nickname);
            }
            if (mRoundRectImageView != null) {
                Picasso.with(this).load(coverUrlMiddle).into(mRoundRectImageView);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
