package com.appchamp.wordchunks.levels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.Level;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class LevelsAdapter extends BaseAdapter {

    private List<Level> levels;

    public LevelsAdapter(List<Level> levels) {
        setLevels(levels);
    }

    public void replaceLevels(List<Level> levels) {
        setLevels(levels);
        notifyDataSetChanged();
    }

    private void setLevels(List<Level> levels) {
        this.levels = checkNotNull(levels, "levels cannot be null");
    }

    @Override
    public int getCount() {
        return levels.size();
    }

    @Override
    public Level getItem(int i) {
        return levels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.level_listitem, viewGroup, false);
        }

        final Level level = getItem(i);

        TextView levelNumberTextView = rowView.findViewById(R.id.level_number);
        levelNumberTextView.setText(String.valueOf(i + 1));
        TextView levelClueTextView = rowView.findViewById(R.id.level_clue);
        levelClueTextView.setText(level.getClue());

        return rowView;
    }
}