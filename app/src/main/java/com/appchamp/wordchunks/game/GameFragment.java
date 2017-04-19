package com.appchamp.wordchunks.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.Level;
import com.appchamp.wordchunks.models.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.google.common.base.Preconditions.checkNotNull;


public class GameFragment extends Fragment implements GameContract.View {

    private GameContract.Presenter mPresenter;

    private WordsAdapter wordsAdapter;

    private ChunksAdapter chunksAdapter;

    private GridView wordsGridView;

    private GridView chunksGridView;

    private Realm realm;

    private long levelId;

    private TextView levelClueTitleTextView;

    private TextView inputFormTextView;

    private ImageView clearIcon;

    public GameFragment() {
        // Requires empty public constructor
    }

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordsAdapter = new WordsAdapter(new ArrayList<>(0));
        chunksAdapter = new ChunksAdapter(new ArrayList<>(0));
    }

    @Override
    public void setPresenter(GameContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter, "presenter cannot be null");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game, container, false);

        levelId = getArguments().getLong("LEVEL_ID");

        levelClueTitleTextView = root.findViewById(R.id.level_clue);

        wordsGridView = root.findViewById(R.id.words_grid);
        wordsGridView.setAdapter(wordsAdapter);

        inputFormTextView = root.findViewById(R.id.inputFormTextView);
        clearIcon = root.findViewById(R.id.clear_icon);

        chunksGridView = root.findViewById(R.id.chunks_grid);
        chunksGridView.setAdapter(chunksAdapter);

        clearIcon.setOnClickListener(v -> {
            Animation animFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            v.startAnimation(animFadeIn);
        });

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(realmConfiguration);

        if (levelId != -1) {
            Level level = realm.where(Level.class).equalTo("id", levelId).findFirst();

            levelClueTitleTextView.setText(level.getClue());

            List<Word> words = level.getWords();

            showWordsGrid(words);

            List<String> chunks = new ArrayList<>(0);
            for (int i = 0; i < words.size(); i++) {
                Collections.addAll(chunks, words.get(i).getChunks().split(","));
            }

            showChunksGrid(chunks);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void showWordsGrid(List<Word> words) {
        wordsAdapter.replaceWords(words);
    }

    @Override
    public void showChunksGrid(List<String> chunks) {
        chunksAdapter.replaceChunks(chunks);
    }

}
