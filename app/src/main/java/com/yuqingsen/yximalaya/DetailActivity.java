package com.yuqingsen.yximalaya;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.yuqingsen.yximalaya.adapters.DetailListAdapter;
import com.yuqingsen.yximalaya.base.BaseActivity;
import com.yuqingsen.yximalaya.interfaces.IAlbumDeatilViewCallback;
import com.yuqingsen.yximalaya.presenters.AlbumDetailPresenter;
import com.yuqingsen.yximalaya.utils.ImageBlur;
import com.yuqingsen.yximalaya.utils.LogUtil;
import com.yuqingsen.yximalaya.views.RoundRectImageView;
import com.yuqingsen.yximalaya.views.UILoader;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDeatilViewCallback, UILoader.OnRetryClickListener {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        albumDetailPresenter = AlbumDetailPresenter.getsInstance();
        albumDetailPresenter.registerViewCallback(this);

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
        }
        largeCover = this.findViewById(R.id.iv_large_cover);
        smallCover = this.findViewById(R.id.riv_small_cover);
        albumTitle = this.findViewById(R.id.tv_album_title);
        albumAuthor = this.findViewById(R.id.tv_album_author);
        uiLoader.setOnRetryClickListener(DetailActivity.this);
    }

    private View creatSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        detailList = detailListView.findViewById(R.id.album_detail_list);
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
        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
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
        };
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
    public void onRetryClick() {
        //表示用户网络不佳的时候点击了重新加载
        if (albumDetailPresenter != null) {
            albumDetailPresenter.getAlbumDetail(mCurrentId, currentPage);
        };
    }
}
