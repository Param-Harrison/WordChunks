package com.appchamp.wordchunks.ui.levels;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.ui.packs.OnPackLevelClickListener;
import com.appchamp.wordchunks.ui.packs.PackLevelViewHolder;
import com.orhanobut.logger.Logger;

import java.util.List;

import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_LOCKED;
import static com.appchamp.wordchunks.util.Constants.PACK_STATE_SOLVED;


public class LevelsAdapter extends RecyclerView.Adapter<PackLevelViewHolder> {

    private List<Level> levels;
    private OnPackLevelClickListener listener;
    private int packColor;

    LevelsAdapter(List<Level> lvls, int color) {
        updateLevels(lvls);
        packColor = color;
    }

    private void updateLevels(List<Level> lvls) {
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

    void setOnItemClickListener(OnPackLevelClickListener listener) {
        this.listener = listener;
    }

    @Override
    public PackLevelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View levelView = inflater.inflate(R.layout.item_pack_level, parent, false);
        return new PackLevelViewHolder(levelView, listener);
    }

    @Override
    public void onBindViewHolder(PackLevelViewHolder holder, int i) {
        final Level level = getItem(i);
        final int levelState = level.getState();
        Resources res = holder.itemView.getResources();
        GradientDrawable drawable = (GradientDrawable) holder.imgRectBg.getDrawable();

        holder.rlItem.setBackground(
                res.getDrawable(R.drawable.main_bg_rect));

        if (levelState == LEVEL_STATE_LOCKED) {
            holder.itemView.setEnabled(false);
            drawable.setColor(
                    res.getColor(R.color.pack_rect_left_locked));
            holder.imgIcon.setImageDrawable(
                    res.getDrawable(R.drawable.ic_locked));
            holder.rlItem.getBackground().setAlpha(100);

            holder.tvItemTitle.setTextColor(
                    res.getColor(R.color.pack_title_txt_locked));
            holder.tvItemSubtitle.setTextColor(
                    res.getColor(R.color.pack_title_txt_locked));

        } else {
            holder.itemView.setEnabled(true);
            drawable.setColor(packColor);

            holder.tvItemTitle.setTextColor(packColor);
            holder.tvItemSubtitle.setTextColor(
                    res.getColor(R.color.pack_num_of_levels_txt));
            if (levelState == PACK_STATE_SOLVED) {
                holder.imgIcon.setImageDrawable(
                        res.getDrawable(R.drawable.ic_solved));
            } else {
                holder.imgIcon.setImageDrawable(
                        res.getDrawable(R.drawable.ic_current));
            }
        }
        holder.tvItemTitle.setText(
                res.getString(R.string.level_title, i + 1));
        holder.tvItemSubtitle.setText(level.getClue());
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    Level getItem(int i) {
        return levels.get(i);
    }

}