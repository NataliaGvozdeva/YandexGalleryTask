package com.example.alexandermelnikov.yandexgallerytask.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.alexandermelnikov.yandexgallerytask.R;
import com.example.alexandermelnikov.yandexgallerytask.utils.Constants;
import com.example.alexandermelnikov.yandexgallerytask.utils.SortMethods;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 23.04.18.
 */

public class SortMethodsDialogAdapter extends RecyclerView.Adapter<SortMethodsDialogAdapter.SortMethodsViewHolder> {

    private static final String TAG = "MyTag";

    Context mContext;
    String selectedMethod;
    int selectedIndex;
    OnSortMethodsItemClickListener listener;

    public SortMethodsDialogAdapter(Context mContext, String selectedMethod,
                                    int selectedIndex, OnSortMethodsItemClickListener listener) {
        this.mContext = mContext;
        this.selectedMethod = selectedMethod;
        this.selectedIndex = selectedIndex;
        this.listener = listener;
    }

    public interface OnSortMethodsItemClickListener {
        void onSortMethodsItemClicked(int position);
    }

    class SortMethodsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rb_status)
        RadioButton rbStatus;
        @BindView(R.id.method_name)
        TextView tvMethodName;

        public SortMethodsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            RxView.clicks(itemView)
                    .subscribe(v -> listener.onSortMethodsItemClicked(getAdapterPosition()));
        }
    }

    @Override
    public SortMethodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SortMethodsViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_item_sort_method, parent, false));
    }

    @Override
    public void onBindViewHolder(SortMethodsViewHolder holder, int position) {
        switch (position) {
            case 0:
                holder.tvMethodName.setText(mContext.getResources().getString(R.string.best));
                break;
            case 1:
                holder.tvMethodName.setText(mContext.getResources().getString(R.string.most_popular));
                break;
            case 2:
                holder.tvMethodName.setText(mContext.getResources().getString(R.string.newest));
                break;
        }

        if (position == selectedIndex) {
            holder.rbStatus.toggle();
        }
    }

    @Override
    public int getItemCount() {
        return Constants.NUMBER_OF_SORT_METHODS;
    }

}