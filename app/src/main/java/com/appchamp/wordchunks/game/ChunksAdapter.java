package com.appchamp.wordchunks.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appchamp.wordchunks.R;

import java.util.List;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;


public class ChunksAdapter extends BaseAdapter {

    private List<String> chunks;

    public ChunksAdapter(List<String> chunks) {
        setChunks(chunks);
    }

    public void replaceChunks(List<String> chunks) {
        setChunks(chunks);
        notifyDataSetChanged();
    }

    private void setChunks(List<String> chunks) {
        this.chunks = checkNotNull(chunks);
    }

    @Override
    public int getCount() {
        return chunks.size();
    }

    @Override
    public String getItem(int i) {
        return chunks.get(i);
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
            rowView = inflater.inflate(R.layout.chunk_griditem, viewGroup, false);
        }

        final String chunk = getItem(i);

        TextView chunkTextView = rowView.findViewById(R.id.chunk);
        chunkTextView.setText(chunk);

        return rowView;
    }
}