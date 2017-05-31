package com.appchamp.wordchunks.ui.packs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;


public class PackLevelViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlItem;
    public TextView tvItemTitle;
    public TextView tvItemSubtitle;
    public ImageView imgRectBg;
    public ImageView imgIcon;

    public PackLevelViewHolder(View itemView, OnPackLevelClickListener listener) {
        super(itemView);

        rlItem = (RelativeLayout) itemView.findViewById(R.id.rlItem);
        tvItemTitle = (TextView) itemView.findViewById(R.id.tvItemTitle);
        tvItemSubtitle = (TextView) itemView.findViewById(R.id.tvItemSubtitle);
        imgRectBg = (ImageView) itemView.findViewById(R.id.imgRectBg);
        imgIcon = (ImageView) itemView.findViewById(R.id.icon);

        // Setup the click listener
        itemView.setOnClickListener(v -> {
            // Triggers click upwards to the adapter on click
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(itemView, position);
                }
            }
        });
    }
}