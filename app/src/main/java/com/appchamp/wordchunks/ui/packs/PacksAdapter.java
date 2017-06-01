package com.appchamp.wordchunks.ui.packs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.LevelsRealmHelper;
import com.appchamp.wordchunks.models.realm.Pack;
import com.orhanobut.logger.Logger;

import java.util.List;

import io.realm.Realm;

import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_SOLVED;
import static com.appchamp.wordchunks.util.Constants.PACK_STATE_LOCKED;
import static com.appchamp.wordchunks.util.Constants.PACK_STATE_SOLVED;


public class PacksAdapter extends RecyclerView.Adapter<PackLevelViewHolder> {

    private List<Pack> packs;
    private OnPackLevelClickListener listener;

    PacksAdapter(List<Pack> p) {
        updatePacks(p);
    }

    private void updatePacks(List<Pack> p) {
        setPacks(p);
        notifyDataSetChanged();
    }

    private void setPacks(List<Pack> packs) {
        if (packs != null) {
            this.packs = packs;
        } else {
            Logger.e("packs cannot be null");
        }
    }

    void setOnItemClickListener(OnPackLevelClickListener listener) {
        this.listener = listener;
    }

    @Override
    public PackLevelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View packView = inflater.inflate(R.layout.item_pack_level, parent, false);
        return new PackLevelViewHolder(packView, listener);
    }

    @Override
    public void onBindViewHolder(PackLevelViewHolder holder, int i) {
        final Pack pack = getItem(i);
        final int packState = pack.getState();
        final int packColor = Color.parseColor(pack.getColor());
        final int packLevelsSize = pack.getLevels().size();
        Resources res = holder.itemView.getResources();
        GradientDrawable drawable = (GradientDrawable) holder.imgRectBg.getDrawable();

        holder.rlItem.setBackground(
                res.getDrawable(R.drawable.main_bg_rect));

        if (packState == PACK_STATE_LOCKED) {
            holder.itemView.setEnabled(false);
            drawable.setColor(
                    res.getColor(R.color.pack_rect_left_locked));
            holder.imgIcon.setImageDrawable(
                    res.getDrawable(R.drawable.ic_locked));
            holder.rlItem.getBackground().setAlpha(100);
            holder.tvItemSubtitle.setText(
                    res.getString(R.string.number_of_levels_locked, packLevelsSize));

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
            final Realm realm = Realm.getDefaultInstance();
            long solvedLevelsSize = LevelsRealmHelper.INSTANCE
                    .countLevelsByPackIdAndState(realm, pack.getId(), LEVEL_STATE_SOLVED);
            realm.close();
            holder.tvItemSubtitle.setText(
                    res.getString(R.string.number_of_levels, solvedLevelsSize, packLevelsSize));
            if (packState == PACK_STATE_SOLVED) {
                holder.imgIcon.setImageDrawable(
                        res.getDrawable(R.drawable.ic_solved));
            } else {
                holder.imgIcon.setImageDrawable(
                        res.getDrawable(R.drawable.ic_current));
            }
        }

        holder.tvItemTitle.setText(
                res.getString(R.string.list_number, (i + 1), pack.getTitle()));
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

}