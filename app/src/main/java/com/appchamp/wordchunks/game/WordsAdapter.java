package com.appchamp.wordchunks.game;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.realm.Word;

import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.ViewHolder> {

    private List<Word> words;

    WordsAdapter(List<Word> words) {
        setWords(words);
    }

    public void updateWords(List<Word> words) {
        setWords(words);
        notifyDataSetChanged();
    }

    private void setWords(List<Word> words) {
        if (words != null) {
            this.words = words;
        } else {
            Log.e("WordsAdapter", "chunks cannot be null");
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    @Override
    public WordsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View levelView = inflater.inflate(R.layout.item_word, parent, false);

        return new WordsAdapter.ViewHolder(levelView);
    }

    @Override
    public void onBindViewHolder(WordsAdapter.ViewHolder holder, int i) {
        final Word word = getItem(i);
        final int wordState = word.getState();

        if (wordState == 0) {
            int wordLength = word.getWord().length();
            holder.tvWordLength.setText(
                    holder.itemView.getResources().getString(R.string.number_of_letters, wordLength));

        } else if (wordState == 1) {
            holder.tvWordNum.setText(word.getWord());
        }
        holder.tvWordNum.setText(String.valueOf(i));
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    private Word getItem(int i) {
        return words.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvWordNum;
        TextView tvWordLength;

        ViewHolder(View itemView) {
            super(itemView);

            tvWordNum = (TextView) itemView.findViewById(R.id.tvWordNum);
            tvWordLength = (TextView) itemView.findViewById(R.id.tvWordLength);
        }
    }
}