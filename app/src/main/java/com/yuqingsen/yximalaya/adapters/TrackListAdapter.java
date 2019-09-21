package com.yuqingsen.yximalaya.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.yuqingsen.yximalaya.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.InnerHolder> {
    private List<Track> detailData = new ArrayList<>();
    //格式化时间
    private SimpleDateFormat updateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat durationFormat = new SimpleDateFormat("mm：ss");
    private ItemClickListener itemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_detail_layout, viewGroup, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder innerHolder, final int i) {
        //找到控件，设置数据显示
        View itemView = innerHolder.itemView;
        //顺序ID
        TextView orderTv = itemView.findViewById(R.id.order_text);
        //标题
        TextView titleTv = itemView.findViewById(R.id.detail_item_title);
        //播放量
        TextView playCountTv = itemView.findViewById(R.id.detail_item_play_count);
        //音频时长
        TextView durationTv = itemView.findViewById(R.id.detail_item_duration);
        //更新日期
        TextView updateTimeTv = itemView.findViewById(R.id.detail_item_update_time);

        //设置数据
        Track track = detailData.get(i);
        orderTv.setText((i + 1) + "");
        titleTv.setText(track.getTrackTitle());
        playCountTv.setText(track.getPlayCount() + "");
        long duration = track.getDuration() * 1000;
        String durationText = durationFormat.format(duration);
        durationTv.setText(durationText);
        String updateTimeText = updateTimeFormat.format(track.getUpdatedAt());
        updateTimeTv.setText(updateTimeText);

        //设置Item点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(detailData, i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return detailData.size();
    }

    public void setData(List<Track> tracks) {
        detailData.clear();
        detailData.addAll(tracks);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(List<Track> detailData, int i);
    }
}
