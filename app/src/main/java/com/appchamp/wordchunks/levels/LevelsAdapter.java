package com.appchamp.wordchunks.levels;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.realm.Level;

import java.util.List;


public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.ViewHolder> {

    private List<Level> levels;
    private LevelsAdapter.OnItemClickListener listener;

    LevelsAdapter(List<Level> lvls) {
        updateLevels(lvls);
    }

    public void updateLevels(List<Level> lvls) {
        setLevels(lvls);
        notifyDataSetChanged();
    }

    private void setLevels(List<Level> lvls) {
        if (lvls != null) {
            this.levels = lvls;
        } else {
            Log.e("LevelsAdapter", "levels cannot be null");
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    void setOnItemClickListener(LevelsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public LevelsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View levelView = inflater.inflate(R.layout.item_level, parent, false);

        return new LevelsAdapter.ViewHolder(levelView);
    }

    @Override
    public void onBindViewHolder(LevelsAdapter.ViewHolder holder, int i) {
        final Level level = getItem(i);
        final int levelState = level.getState();

        if (levelState == 1) {
            holder.itemView.setEnabled(true);
            holder.levelLayout.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.accent));
        } else if (levelState == 0) {
            holder.itemView.setEnabled(false);
            holder.levelLayout.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.primary_dark));
        } else if (levelState == 2) {
            holder.itemView.setEnabled(true);
            holder.levelLayout.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.secondary_text));
        }

        int levelNum = i + 1;
        holder.levelNameTextView.setText(
                holder.itemView.getResources().getString(R.string.level_num, levelNum));
        holder.levelNumberTextView.setText(String.valueOf(levelNum));
        holder.clueTextView.setText(level.getClue());
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    Level getItem(int i) {
        return levels.get(i);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout levelLayout;
        TextView levelNameTextView;
        TextView levelNumberTextView;
        TextView clueTextView;

        ViewHolder(View itemView) {
            super(itemView);

            levelLayout = (RelativeLayout) itemView.findViewById(R.id.level_layout);
            levelNameTextView = (TextView) itemView.findViewById(R.id.level_name);
            levelNumberTextView = (TextView) itemView.findViewById(R.id.level_number);
            clueTextView = (TextView) itemView.findViewById(R.id.tvLevelClueTitle);

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