package com.appchamp.wordchunks.ui.game.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.realm.Word;
import com.orhanobut.logger.Logger;

import java.util.List;

import static com.appchamp.wordchunks.util.Constants.WORD_STATE_NOT_SOLVED;
import static com.appchamp.wordchunks.util.Constants.WORD_STATE_SOLVED;


public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.ViewHolder> {

    private List<Word> words;
    private int packColor;

    public WordsAdapter(List<Word> words, int color) {
        updateWords(words);
        this.packColor = color;
    }

    private void updateWords(List<Word> words) {
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
        Resources res = holder.itemView.getResources();

        GradientDrawable drawable = (GradientDrawable) holder.imgRectBg.getDrawable();

        switch (wordState) {
            case WORD_STATE_NOT_SOLVED:
                int wordLength = word.getWord().length();
                holder.tvWord.setText(
                        res.getString(R.string.number_of_letters, wordLength));
                holder.tvWordNum.setText(getWordIndex(i));
                drawable.setColor(
                        res.getColor(R.color.word_rect_bg));
                break;
            case WORD_STATE_SOLVED:
                holder.tvWord.setText(word.getWord());
                holder.tvWord.setTextColor(packColor);
                holder.imgIcon.setVisibility(View.VISIBLE);
                holder.tvWordNum.setVisibility(View.GONE);
                drawable.setColor(packColor);
                break;
        }

        // if right column
        if (i == 1 || i == 3 || i == 5) {
            setItemLayout(holder, RelativeLayout.ALIGN_PARENT_END, Gravity.START, res);
        } else {
            // if left column
            setItemLayout(holder, RelativeLayout.ALIGN_PARENT_START, Gravity.END, res);
        }
    }

    private void setItemLayout(ViewHolder holder, int alignParent, int gravity, Resources res) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        params.addRule(alignParent, RelativeLayout.TRUE);

        holder.imgIcon.setLayoutParams(params);
        holder.imgRectBg.setLayoutParams(params);
        holder.tvWord.setGravity(gravity | Gravity.CENTER_VERTICAL);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 32, res.getDisplayMetrics()),
                RelativeLayout.LayoutParams.MATCH_PARENT);

        params2.addRule(alignParent, RelativeLayout.TRUE);
        holder.tvWordNum.setLayoutParams(params2);
    }

    private String getWordIndex(int i) {
        if (i == 0) return "1";
        if (i == 1) return "4";
        if (i == 2) return "2";
        if (i == 3) return "5";
        if (i == 4) return "3";
        if (i == 5) return "6";
        return "";
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvWord;
        TextView tvWordNum;
        ImageView imgIcon;
        ImageView imgRectBg;

        ViewHolder(View itemView) {
            super(itemView);

            tvWord = (TextView) itemView.findViewById(R.id.tvWord);
            tvWordNum = (TextView) itemView.findViewById(R.id.tvWordNum);
            imgIcon = (ImageView) itemView.findViewById(R.id.icon);
            imgRectBg = (ImageView) itemView.findViewById(R.id.imgRectBg);
        }
    }
}