package com.appchamp.wordchunks.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appchamp.wordchunks.R;


public class PackLevelViewHolder extends RecyclerView.ViewHolder {

    public CardView cvItem;
    public TextView tvItemTitle;
    public TextView tvItemNum;
    public TextView tvItemSubtitle;
    public ImageView imgRectBg;
    public ImageView imgIcon;

    public PackLevelViewHolder(View itemView, OnPackLevelClickListener listener) {
        super(itemView);

        cvItem = (CardView) itemView.findViewById(R.id.cvItem);
        tvItemTitle = (TextView) itemView.findViewById(R.id.tvItemTitle);
        tvItemNum = (TextView) itemView.findViewById(R.id.tvItemNum);
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