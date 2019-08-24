package com.yuqingsen.yximalaya;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.yuqingsen.yximalaya.adapters.IndicatorAdapter;
import com.yuqingsen.yximalaya.adapters.MainContentAdapter;
import com.yuqingsen.yximalaya.interfaces.IPlayerCallback;
import com.yuqingsen.yximalaya.presenters.PlayerPresenter;
import com.yuqingsen.yximalaya.presenters.RecommendPresenter;
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
    private View mPlayCoverControl;
    private View mSearchBtn;

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
                LogUtil.d(TAG, "click index is" + i);
                if (contentPager != null) {
                    contentPager.setCurrentItem(i);
                }
            }
        });
        mCoverPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没有设置过播放列表就播放第一个节目
                        playFirstRecommend();
                    } else {
                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        } else {
                            mPlayerPresenter.play();
                        }
                    }
                }
            }
        });
        mPlayCoverControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPlayList = mPlayerPresenter.hasPlayList();
                if (!hasPlayList) {
                    playFirstRecommend();
                }
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 播放第一个推荐的内容
     */
    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getsInstance().getCurrentRecommend();
        if (currentRecommend != null) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            mPlayerPresenter.playByAlbumId(albumId);
        }
    }

    private void initView() {
        magicIndicator = this.findViewById(R.id.main_indicator);
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
        ViewPagerHelper.bind(magicIndicator, contentPager);

        //底部播放控制相关
        mRoundRectImageView = this.findViewById(R.id.track_main_cover);
        mCoverTitle = this.findViewById(R.id.track_main_cover_title);
        mCoverTitle.setSelected(true);
        mCoverAuthor = this.findViewById(R.id.track_main_cover_author);
        mCoverPlayControl = this.findViewById(R.id.track_main_cover_play_control);
        mPlayCoverControl = this.findViewById(R.id.main_play_cover_control);
        mSearchBtn = this.findViewById(R.id.search_btn_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
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
