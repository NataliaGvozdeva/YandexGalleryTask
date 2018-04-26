package com.example.alexandermelnikov.yandexgallerytask.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexandermelnikov.yandexgallerytask.R;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageRequest;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    Context mContext;
    ArrayList<ImageRequest> mData;
    OnHistoryItemClickListener listener;

    public HistoryAdapter(Context mContext, ArrayList<ImageRequest> mData, OnHistoryItemClickListener listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.listener = listener;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_query)
        TextView tvQuery;

        public HistoryViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            RxView.clicks(view)
                    .subscribe(v -> listener.onHistoryItemClicked(tvQuery.getText().toString()));
        }
    }

    public interface OnHistoryItemClickListener {
        void onHistoryItemClicked(String requestPhrase);
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        ImageRequest imageRequest = mData.get(position);
        holder.tvQuery.setText(imageRequest.getPhrase());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void replaceData(ArrayList<ImageRequest> imageRequests) {
        mData.clear();
        mData.addAll(imageRequests);
        notifyDataSetChanged();
    }
}
