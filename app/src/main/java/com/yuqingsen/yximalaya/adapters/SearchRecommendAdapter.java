package com.yuqingsen.yximalaya.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.yuqingsen.yximalaya.R;

import java.util.ArrayList;
import java.util.List;

public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.InnerHolder> {
    private List<QueryResult> mData = new ArrayList<>();
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_recommend,viewGroup,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder innerHolder, int i) {
        TextView text = innerHolder.itemView.findViewById(R.id.search_recommend_item);
        QueryResult queryResult = mData.get(i);
        text.setText(queryResult.getKeyword());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 设置数据
     * @param keyWordList
     */
    public void setData(List<QueryResult> keyWordList) {
        mData.clear();
        mData.addAll(keyWordList);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
