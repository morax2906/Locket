package com.tandev.locket.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.tandev.locket.R;
import com.tandev.locket.test.MomentEntity;
import java.util.List;

public class ViewAllMomentAdapter extends RecyclerView.Adapter<ViewAllMomentAdapter.ViewHolder> {
    private List<MomentEntity> itemList; // Danh sách URL hình ảnh hoặc dữ liệu
    private final Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(List<MomentEntity> filterList) {
        this.itemList = filterList;
        notifyDataSetChanged();
    }

    public ViewAllMomentAdapter(List<MomentEntity> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_moment, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MomentEntity moment = itemList.get(position);

        Glide.with(context)
                .load(moment.getThumbnailUrl())
                .into(holder.shapeable_imageview);

        holder.itemView.setOnClickListener(view -> {

        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView shapeable_imageview;

        public ViewHolder(View itemView) {
            super(itemView);
            shapeable_imageview = itemView.findViewById(R.id.shapeable_imageview);
        }
    }

}
