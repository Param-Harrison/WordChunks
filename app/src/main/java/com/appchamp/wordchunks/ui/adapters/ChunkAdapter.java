package com.appchamp.wordchunks.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appchamp.wordchunks.R;

import java.util.List;


public class ChunkAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<String> chunks = null;

    public ChunkAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<String> parts) {
        this.chunks = parts;
    }

    @Override
    public int getCount() {
        if (chunks == null) {
            return 0;
        }
        return chunks.size();
    }

    @Override
    public Object getItem(int position) {
        if (chunks == null || chunks.get(position) == null) {
            return null;
        }
        return chunks.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.chunk_griditem, parent, false);
        }

        String chunk = chunks.get(position);

        if (chunk != null) {
            ((TextView) currentView.findViewById(R.id.chunk)).setText(chunk);
        }

        return currentView;
    }
}