package com.yuqingsen.yximalaya.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.yuqingsen.yximalaya.R;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {
    private static final String TAG = "AlbumListAdapter";

    private List<Album> data = new ArrayList<>();
    private OnRecommendItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recommend, viewGroup, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder innerHolder, int i) {
        innerHolder.itemView.setTag(i);
        innerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    int clickPosition = (int) v.getTag();
                    mItemClickListener.onItemClick(clickPosition, data.get(clickPosition));
                }
            }
        });
        innerHolder.setData(data.get(i));
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        if (data != null) {
            data.clear();
            data.addAll(albumList);
        }
        //更新UI
        notifyDataSetChanged();
    }

    public int getDataSize() {
        return data.size();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            if (album != null) {

                //找到控件，设置数据
                //专辑封面
                ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
                //标题
                TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
                //描述
                TextView albumDesTv = itemView.findViewById(R.id.album_description_tv);
                //播放数量
                TextView albumPlayCount = itemView.findViewById(R.id.album_play_count);
                //专辑内容数量
                TextView albumContentSize = itemView.findViewById(R.id.album_content_size);

                albumTitleTv.setText(album.getAlbumTitle());
                albumDesTv.setText(album.getAlbumIntro());
                albumPlayCount.setText(album.getPlayCount() + "");
                albumContentSize.setText(album.getIncludeTrackCount() + "");

                String url = album.getCoverUrlLarge();
                if (!TextUtils.isEmpty(url)) {
                    Picasso.with(itemView.getContext()).load(url).into(albumCoverIv);
                }else {
                    albumCoverIv.setImageResource(R.mipmap.ic_fm);
                }
            }
        }
    }

    public void setOnRecommendItemClickListener(OnRecommendItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnRecommendItemClickListener {
        void onItemClick(int position, Album album);
    }
}
