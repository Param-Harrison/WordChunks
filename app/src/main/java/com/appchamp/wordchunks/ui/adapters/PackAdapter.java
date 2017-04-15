package com.appchamp.wordchunks.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.Pack;

import java.util.List;


public class PackAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Pack> packs = null;

    public PackAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Pack> details) {
        this.packs = details;
    }

    @Override
    public int getCount() {
        if (packs == null) {
            return 0;
        }
        return packs.size();
    }

    @Override
    public Object getItem(int position) {
        if (packs == null || packs.get(position) == null) {
            return null;
        }
        return packs.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.pack_listitem, parent, false);
        }

        Pack pack = packs.get(position);

        if (pack != null) {
            ((TextView) currentView.findViewById(R.id.pack_name)).setText(pack.getName());
            ((TextView) currentView.findViewById(R.id.pack_number))
                    .setText(String.valueOf(position + 1));
            ((TextView) currentView.findViewById(R.id.number_of_levels))
                    .setText(String.valueOf(pack.getLevels().size())+" levels");
        }

        return currentView;
    }
}