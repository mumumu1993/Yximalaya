package com.yuqingsen.yximalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.yuqingsen.yximalaya.adapters.DetailListAdapter;
import com.yuqingsen.yximalaya.base.BaseActivity;
import com.yuqingsen.yximalaya.base.BaseApplication;
import com.yuqingsen.yximalaya.interfaces.IAlbumDeatilViewCallback;
import com.yuqingsen.yximalaya.interfaces.IPlayerCallback;
import com.yuqingsen.yximalaya.presenters.AlbumDetailPresenter;
import com.yuqingsen.yximalaya.presenters.PlayerPresenter;
import com.yuqingsen.yximalaya.utils.ImageBlur;
import com.yuqingsen.yximalaya.utils.LogUtil;
import com.yuqingsen.yximalaya.views.RoundRectImageView;
import com.yuqingsen.yximalaya.views.UILoader;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.BezierPagerIndicator;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDeatilViewCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener, IPlayerCallback {
    private static final String TAG = "DetailActivity";
    private ImageView largeCover;
    private RoundRectImageView smallCover;
    private TextView albumTitle;
    private TextView albumAuthor;
    private AlbumDetailPresenter albumDetailPresenter;
    private int currentPage = 1;
    private RecyclerView detailList;
    private DetailListAdapter detailListAdapter;
    private UILoader uiLoader;
    private FrameLayout detailListContainer;
    private long mCurrentId = -1;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTips;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks = null;
    private final static int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mTrackTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        //专辑详情的Presenter
        albumDetailPresenter = AlbumDetailPresenter.getsInstance();
        albumDetailPresenter.registerViewCallback(this);
        //播放器的Presenter
        mPlayerPresenter = PlayerPresenter.getsPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        updatePlayState(mPlayerPresenter.isPlaying());
        initListener();
    }

    /**
     * 根据播放状态显示对应的图标和文字
     *
     * @param playing
     */
    private void updatePlayState(boolean playing) {
        if (mPlayControlBtn != null && mPlayControlTips != null) {
            mPlayControlBtn.setImageResource(playing ? R.drawable.selector_play_control_stop : R.drawable.selector_play_control_play);
            if (!playing) {
                mPlayControlTips.setText(R.string.click_play_tips_text);
            }else {
                if (!TextUtils.isEmpty(mTrackTitle)) {
                    mPlayControlTips.setText(mTrackTitle);
                }
            }
        }
    }

    private void initListener() {
        if (mPlayControlBtn != null) {
            mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlayerPresenter != null) {
                        //判断播放器是否有播放列表
                        //todo
                        boolean hasPlayList = mPlayerPresenter.hasPlayList();

                        if (hasPlayList) {
                            //控制播放器状态
                            handlePlayControl();
                        } else {
                            handleNoPlayList();
                        }
                    }
                }
            });
        }
    }

    /**
     * 播放器里面没有播放内容时进行处理
     */
    private void handleNoPlayList() {
        mPlayerPresenter.setPlayList(mCurrentTracks, DEFAULT_PLAY_INDEX);
    }

    private void handlePlayControl() {
        if (mPlayerPresenter.isPlaying()) {
            //正在播放就暂停
            mPlayerPresenter.pause();
        } else {
            mPlayerPresenter.play();
        }
    }

    private void initView() {
        detailListContainer = this.findViewById(R.id.detail_list_container);
        if (uiLoader == null) {
            uiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return creatSuccessView(container);
                }
            };
            detailListContainer.removeAllViews();
            detailListContainer.addView(uiLoader);
            uiLoader.setOnRetryClickListener(DetailActivity.this);
        }
        largeCover = this.findViewById(R.id.iv_large_cover);
        smallCover = this.findViewById(R.id.riv_small_cover);
        albumTitle = this.findViewById(R.id.tv_album_title);
        albumAuthor = this.findViewById(R.id.tv_album_author);
        //播放控制的图标
        mPlayControlBtn = this.findViewById(R.id.detail_play_control);
        mPlayControlTips = this.findViewById(R.id.detail_play_control_tv);
        mPlayControlTips.setSelected(true);

    }

    private boolean mIsLoaderMore = false;

    private View creatSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        detailList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        detailList.setLayoutManager(layoutManager);
        //设置适配器
        detailListAdapter = new DetailListAdapter();
        detailList.setAdapter(detailListAdapter);
        detailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        detailListAdapter.setItemClickListener(this);
        BezierLayout headView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headView);
        mRefreshLayout.setHeaderHeight(140);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                if (albumDetailPresenter != null) {
                    albumDetailPresenter.loadMore();
                    mIsLoaderMore = true;
                }
            }

            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                //todo

            }
        });
        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (mIsLoaderMore && mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
            mIsLoaderMore = false;
        }

        this.mCurrentTracks = tracks;
        //判断数据结果，根据结果显示UI
        if (tracks == null || tracks.size() == 0) {
            if (uiLoader != null) {
                uiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }
        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //更新UI数据
        detailListAdapter.setData(tracks);
    }

    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        uiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);

    }

    @Override
    public void onAlbumLoaded(Album album) {
        mCurrentId = album.getId();
        if (albumDetailPresenter != null) {
            albumDetailPresenter.getAlbumDetail(mCurrentId, currentPage);
        }
        ;
        //拿数据显示Loading状态
        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        if (albumTitle != null) {
            albumTitle.setText(album.getAlbumTitle());
        }
        if (albumAuthor != null) {
            albumAuthor.setText(album.getAnnouncer().getNickname());
        }
        if (largeCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(largeCover, new Callback() {
                @Override
                public void onSuccess() {
                    Drawable drawable = largeCover.getDrawable();
                    if (drawable != null) {
                        ImageBlur.makeBlur(largeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError() {
                    LogUtil.d(TAG, "onError");
                }
            });
        }
        if (smallCover != null) {
            Picasso.with(this).load(album.getCoverUrlSmall()).into(smallCover);
        }
    }

    @Override
    public void onLoaderMoreFinished(int size) {
        if (size>0) {
            Toast.makeText(this,"成功加载"+size+"条节目",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"没有更多节目",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    @Override
    public void onRetryClick() {
        //表示用户网络不佳的时候点击了重新加载
        if (albumDetailPresenter != null) {
            albumDetailPresenter.getAlbumDetail(mCurrentId, currentPage);
        }
        ;
    }

    @Override
    public void onItemClick(List<Track> detailData, int i) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getsPlayerPresenter();
        playerPresenter.setPlayList(detailData, i);

        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPlayStart() {
        //修改图标为暂停样式,文字修改成正在播放
        updatePlayState(true);

    }

    @Override
    public void onPlayPause() {
        //修改图标为播放样式，文字修改成已暂停
        updatePlayState(false);
    }

    @Override
    public void onplayStop() {
        updatePlayState(false);
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
            mTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mTrackTitle)&&mPlayControlTips!=null) {
                mPlayControlTips.setText(mTrackTitle);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
