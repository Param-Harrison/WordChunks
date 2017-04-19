package com.appchamp.wordchunks.packs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.Pack;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class PacksAdapter extends BaseAdapter {

    private List<Pack> packs;

    public PacksAdapter(List<Pack> packs) {
        setPacks(packs);
    }

    public void replacePacks(List<Pack> packs) {
        setPacks(packs);
        notifyDataSetChanged();
    }

    private void setPacks(List<Pack> packs) {
        this.packs = checkNotNull(packs, "packs cannot be null");
    }

    @Override
    public int getCount() {
        return packs.size();
    }

    @Override
    public Pack getItem(int i) {
        return packs.get(i);
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
            rowView = inflater.inflate(R.layout.pack_listitem, viewGroup, false);
        }

        final Pack pack = getItem(i);

        TextView packNameTextView = rowView.findViewById(R.id.pack_name);
        packNameTextView.setText(pack.getName());

        TextView packNumberTextView = rowView.findViewById(R.id.pack_number);
        packNumberTextView.setText(String.valueOf(i + 1));

        TextView numberOfLevelsTextView = rowView.findViewById(R.id.number_of_levels);
        numberOfLevelsTextView.setText(
                viewGroup.getContext().getString(R.string.number_of_levels,
                        pack.getLevels().size()));

        return rowView;
    }
}