package com.appchamp.wordchunks.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.Word;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class WordsAdapter extends BaseAdapter {

    private List<Word> words;

    public WordsAdapter(List<Word> words) {
        setWords(words);
    }

    public void replaceWords(List<Word> words) {
        setWords(words);
        notifyDataSetChanged();
    }

    private void setWords(List<Word> words) {
        this.words = checkNotNull(words, "words cannot be null");
    }

    @Override
    public int getCount() {
        return words.size();
    }

    @Override
    public Word getItem(int i) {
        return words.get(i);
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
            rowView = inflater.inflate(R.layout.word_griditem, viewGroup, false);
        }

        final Word word = getItem(i);

        TextView wordNumberTextView = rowView.findViewById(R.id.word_number);
        wordNumberTextView.setText("1");

        TextView wordLengthTextView = rowView.findViewById(R.id.word_length);
        int wordLength = word.getWord().length();
        wordLengthTextView.setText(
                viewGroup.getContext().getString(R.string.number_of_letters, wordLength));

        return rowView;
    }
}