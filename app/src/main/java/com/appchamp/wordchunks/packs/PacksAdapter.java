package com.appchamp.wordchunks.packs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.realm.Pack;

import java.util.List;


public class PacksAdapter extends RecyclerView.Adapter<PacksAdapter.ViewHolder> {

    private List<Pack> packs;
    private OnItemClickListener listener;

    PacksAdapter(List<Pack> p) {
        updatePacks(p);
    }

    public void updatePacks(List<Pack> p) {
        setPacks(p);
        notifyDataSetChanged();
    }

    private void setPacks(List<Pack> packs) {
        if (packs != null) {
            this.packs = packs;
        } else {
            Log.e("PacksAdapter", "packs cannot be null");
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public PacksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View packView = inflater.inflate(R.layout.item_pack, parent, false);

        return new ViewHolder(packView);
    }

    @Override
    public void onBindViewHolder(PacksAdapter.ViewHolder holder, int i) {
        final Pack pack = getItem(i);
        final int packState = pack.getState();

        if (packState == 1) {
            holder.itemView.setEnabled(true);
            holder.packLayout.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.accent));
        } else if (packState == 0) {
            holder.itemView.setEnabled(false);
            holder.packLayout.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.primary_dark));

        } else if (packState == 2) {
            holder.itemView.setEnabled(true);
            holder.packLayout.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.primary));
        }

        holder.packNameTextView.setText(pack.getTitle());
        holder.packNumberTextView.setText(String.valueOf(i + 1));
        holder.numberOfLevelsTextView.setText(
                holder.itemView.getResources().getString(
                        R.string.number_of_levels, pack.getLevels().size()));
    }

    @Override
    public int getItemCount() {
        return packs.size();
    }

    Pack getItem(int i) {
        return packs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout packLayout;
        TextView packNameTextView;
        TextView packNumberTextView;
        TextView numberOfLevelsTextView;

        ViewHolder(View itemView) {
            super(itemView);

            packLayout = (RelativeLayout) itemView.findViewById(R.id.pack_layout);
            packNameTextView = (TextView) itemView.findViewById(R.id.pack_name);
            packNumberTextView = (TextView) itemView.findViewById(R.id.pack_number);
            numberOfLevelsTextView = (TextView) itemView.findViewById(R.id.number_of_levels);

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
}