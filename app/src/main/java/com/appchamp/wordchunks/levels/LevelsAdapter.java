package com.appchamp.wordchunks.levels;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.realm.Level;
import com.orhanobut.logger.Logger;

import java.util.List;

import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_CURRENT;
import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_LOCKED;
import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_SOLVED;


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
            Logger.d("levels cannot be null");
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

        if (levelState == LEVEL_STATE_LOCKED) {
            holder.itemView.setEnabled(false);
            holder.rlLevel.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.primary_dark));
        } else  if (levelState == LEVEL_STATE_CURRENT) {
            holder.itemView.setEnabled(true);
            holder.rlLevel.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.accent));
        } else if (levelState == LEVEL_STATE_SOLVED) {
            holder.itemView.setEnabled(true);
            holder.rlLevel.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.secondary_text));
        }

        int levelNum = i + 1;
        holder.tvLevelName.setText(
                holder.itemView.getResources().getString(R.string.level_num, levelNum));
        holder.tvLevelNumber.setText(String.valueOf(levelNum));
        holder.tvLevelClueTitle.setText(level.getClue());
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    Level getItem(int i) {
        return levels.get(i);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlLevel;
        TextView tvLevelName;
        TextView tvLevelNumber;
        TextView tvLevelClueTitle;

        ViewHolder(View itemView) {
            super(itemView);

            rlLevel = (RelativeLayout) itemView.findViewById(R.id.rlLevel);
            tvLevelName = (TextView) itemView.findViewById(R.id.tvLevelName);
            tvLevelNumber = (TextView) itemView.findViewById(R.id.tvLevelNumber);
            tvLevelClueTitle = (TextView) itemView.findViewById(R.id.tvLevelClueTitle);

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