package com.appchamp.wordchunks.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.Word;

import java.util.List;


public class WordAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Word> words = null;

    public WordAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Word> words) {
        this.words = words;
    }

    @Override
    public int getCount() {
        if (words == null) {
            return 0;
        }
        return words.size();
    }

    @Override
    public Object getItem(int position) {
        if (words == null || words.get(position) == null) {
            return null;
        }
        return words.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.word_listitem, parent, false);
        }

        Word word = words.get(position);

        if (word != null) {
            ((TextView) currentView.findViewById(R.id.word_number))
                    .setText(String.valueOf(position + 1));
            ((TextView) currentView.findViewById(R.id.word))
                    .setText(String.valueOf(word.getWord().length()) + " letters");
        }

        return currentView;
    }
}