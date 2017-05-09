package com.appchamp.wordchunks.game;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.realm.Word;
import com.appchamp.wordchunks.util.RealmUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import static com.appchamp.wordchunks.util.Constants.NUMBER_OF_WORDS;
import static com.appchamp.wordchunks.util.Constants.WORD_STATE_NOT_SOLVED;
import static com.appchamp.wordchunks.util.Constants.WORD_STATE_SOLVED;


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
            Logger.e("words cannot be null");
        }
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
        final Word word = words.get(i);
        final int wordState = word.getState();

        if (wordState == WORD_STATE_NOT_SOLVED) {
            int wordLength = word.getWord().length();
            holder.tvWord.setText(
                    holder.itemView.getResources().getString(R.string.number_of_letters, wordLength));

        } else if (wordState == WORD_STATE_SOLVED) {
            holder.tvWord.setText(word.getWord());
            holder.tvWord.setTextColor(holder.itemView.getResources().getColor(R.color.accent));
        }
        String wordNum = RealmUtils.getWordNum(i);
        holder.tvWordNum.setText(wordNum);
    }

    @Override
    public int getItemCount() {
        return NUMBER_OF_WORDS;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvWordNum;
        TextView tvWord;

        ViewHolder(View itemView) {
            super(itemView);

            tvWordNum = (TextView) itemView.findViewById(R.id.tvWordNum);
            tvWord = (TextView) itemView.findViewById(R.id.tvWord);
        }
    }
}