package com.yuqingsen.yximalaya.adapters;

import android.content.ContentUris;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.yuqingsen.yximalaya.R;
import com.yuqingsen.yximalaya.base.BaseApplication;
import com.yuqingsen.yximalaya.views.SobPopWindow;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {

    private List<Track> mData = new ArrayList<>();
    private int playingIndex = 0;
    private SobPopWindow.PlayListItemClickListener mItemClickListener = null;

    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_play_list, viewGroup, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder innerHolder, final int i) {
        innerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(i);
                }
            }
        });
        //设置数据
        Track track = mData.get(i);
        TextView trackTitleTv = innerHolder.itemView.findViewById(R.id.track_title_tv);

        trackTitleTv.setTextColor(BaseApplication.getAppContext().getResources().getColor(playingIndex == i ?R.color.main_color:R.color.sub_text_title));
        trackTitleTv.setText(track.getTrackTitle());
        //找到播放状态的图标
        View playIconView = innerHolder.itemView.findViewById(R.id.play_icon_iv);
        playIconView.setVisibility(playingIndex == i ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Track> data) {
        //设置数据更新列表
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPosition(int position) {
        playingIndex = position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(SobPopWindow.PlayListItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
