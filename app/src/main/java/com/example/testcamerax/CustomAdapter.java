package com.example.testcamerax;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<OcrRecord> mDataList;
    private final Context mContext;

    public CustomAdapter(Context context, List<OcrRecord> initialList) {
        this.mContext = context.getApplicationContext();
        this.mDataList = initialList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView timeView;
        TextView contentView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            timeView = itemView.findViewById(R.id.item_time);
            contentView = itemView.findViewById(R.id.item_text);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleview_liner, parent, false);
        return new ViewHolder(view);
    }


    //条目加载
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OcrRecord record = mDataList.get(position);

        String uri = record.getImageUri().toString();

        if (!uri.isEmpty()) {
            Glide.with(mContext)
                    .load(uri)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background); // 或其他默认图
        }

        String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new java.util.Date(record.getTimestamp()));

        holder.timeView.setText(formattedTime);

        String preview = record.getRecognizedText();
        holder.contentView.setText(preview.length() > 50 ? preview.substring(0, 50) + "..." : preview);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra("RECORD_ID", record.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    // 更高效的数据更新（使用 DiffUtil）
    public void updateData(List<OcrRecord> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return mDataList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return mDataList.get(oldItemPosition).getId() ==
                        newList.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return mDataList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }
        });

        mDataList = newList;
        diffResult.dispatchUpdatesTo(this);
    }
}