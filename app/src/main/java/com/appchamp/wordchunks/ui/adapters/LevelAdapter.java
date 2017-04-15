package com.appchamp.wordchunks.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.Level;

import java.util.List;


public class LevelAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Level> levels = null;

    public LevelAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Level> levels) {
        this.levels = levels;
    }

    @Override
    public int getCount() {
        if (levels == null) {
            return 0;
        }
        return levels.size();
    }

    @Override
    public Object getItem(int position) {
        if (levels == null || levels.get(position) == null) {
            return null;
        }
        return levels.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.level_listitem, parent, false);
        }

        Level level = levels.get(position);

        if (level != null) {
            ((TextView) currentView.findViewById(R.id.level_number))
                    .setText(String.valueOf(position + 1));
            ((TextView) currentView.findViewById(R.id.level_clue))
                    .setText(String.valueOf(level.getClue()));
        }
        return currentView;
    }
}