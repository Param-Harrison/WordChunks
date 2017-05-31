package com.appchamp.wordchunks.ui.game.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.ChunksRealmHelper;
import com.appchamp.wordchunks.data.LevelsRealmHelper;
import com.appchamp.wordchunks.data.PacksRealmHelper;
import com.appchamp.wordchunks.data.WordsRealmHelper;
import com.appchamp.wordchunks.models.realm.Chunk;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Word;
import com.appchamp.wordchunks.ui.game.CustomGridLayoutManager;
import com.appchamp.wordchunks.ui.game.adapters.ChunksAdapter;
import com.appchamp.wordchunks.ui.game.adapters.WordsAdapter;
import com.appchamp.wordchunks.ui.game.listeners.OnLevelSolvedListener;
import com.appchamp.wordchunks.util.AnimUtils;
import com.appchamp.wordchunks.util.RealmUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.appchamp.wordchunks.util.Constants.CHUNKS_GRID_NUM;
import static com.appchamp.wordchunks.util.Constants.CHUNK_STATE_GONE;
import static com.appchamp.wordchunks.util.Constants.CHUNK_STATE_NORMAL;
import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.WORDS_GRID_NUM;
import static com.appchamp.wordchunks.util.Constants.WORD_STATE_SOLVED;


public class GameFrag extends Fragment {

    private Realm realm;
    private OnLevelSolvedListener callback;
    private Level level;

    // Views
    private LinearLayout llPackBg;
    private RecyclerView rvWords;
    private RecyclerView rvChunks;
    private TextView tvLevelClueTitle;
    private TextView tvInputChunks;
    private ImageView imgClearIcon;
    private ImageView btnShuffle;
    private ImageView btnHint;

    // Adapters
    private WordsAdapter wordsAdapter;
    private ChunksAdapter chunksAdapter;

    public GameFrag() {
        // Requires empty public constructor
    }

    public static GameFrag newInstance() {
        return new GameFrag();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnLevelSolvedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLevelSolvedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Setup initial views
        View root = inflater.inflate(R.layout.frag_game, container, false);
        llPackBg = (LinearLayout) root.findViewById(R.id.llPackBg);
        tvLevelClueTitle = (TextView) root.findViewById(R.id.tvLevelClueTitle);
        rvWords = (RecyclerView) root.findViewById(R.id.rvWords);
        rvChunks = (RecyclerView) root.findViewById(R.id.rvChunks);
        tvInputChunks = (TextView) root.findViewById(R.id.tvInputChunks);
        imgClearIcon = (ImageView) root.findViewById(R.id.imgClearIcon);
        btnShuffle = (ImageView) root.findViewById(R.id.btnShuffle);
        btnHint = (ImageView) root.findViewById(R.id.btnHint);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        realm = Realm.getDefaultInstance();

        String levelId = getArguments().getString(EXTRA_LEVEL_ID);

        level = LevelsRealmHelper.findLevelById(realm, levelId);

        initLevelClueTitle(level.getClue());

        int packColor = Color.parseColor(
                PacksRealmHelper.findFirstPackById(realm, level.getPackId())
                .getColor());

        llPackBg.setBackgroundColor(packColor);

        initWordsAdapter(level.getWords(), packColor);
        initChunksAdapter(level.getChunks());

        updateInputChunksTextView();

        // Sets click listeners
        imgClearIcon.setOnClickListener(this::onClearIconClick);
        //btnSend.setOnClickListener(this::isWordSolved);
        btnShuffle.setOnClickListener(this::onShuffleClick);
        btnHint.setOnClickListener(this::onHintClick);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    private void initLevelClueTitle(String clue) {
        tvLevelClueTitle.setText(clue);
    }

    private void initWordsAdapter(List<Word> words, int packColor) {
        wordsAdapter = new WordsAdapter(words, packColor);
        rvWords.setAdapter(wordsAdapter);
        rvWords.setLayoutManager(
                new CustomGridLayoutManager(getActivity(), WORDS_GRID_NUM));
        rvWords.setHasFixedSize(true);
    }

    private void initChunksAdapter(List<Chunk> chunks) {
        chunksAdapter = new ChunksAdapter(chunks);
        chunksAdapter.setHasStableIds(true);
        rvChunks.setAdapter(chunksAdapter);
        rvChunks.setLayoutManager(
                new CustomGridLayoutManager(getActivity(), CHUNKS_GRID_NUM));
        //rvChunks.addItemDecoration(new VerticalGridCardSpacingDecoration());
        rvChunks.setHasFixedSize(true);
        chunksAdapter.setOnItemClickListener(
                (itemView, pos) -> onChunkClick(chunks, pos));
    }

    /**
     * Clicks methods.
     */
    private void onClearIconClick(View v) {
        AnimUtils.startAnimationFadeIn(getContext(), v);
        clearChunksStates();
        updateInputChunksTextView();
    }

    private void onShuffleClick(View v) {
        int chunksSize = level.getChunks().size();
        int[] a = RealmUtils.shuffleArray(chunksSize);
        realm.executeTransaction(bgRealm -> {
            for (int i = 0; i < chunksSize / 2; i++) {
                level.getChunks().get(a[i]).setPosition(a[chunksSize - i - 1]);
                level.getChunks().get(a[chunksSize - i - 1]).setPosition(a[i]);
            }
        });
        chunksAdapter.notifyDataSetChanged();
    }

    private void onHintClick(View v) {
        // todo
    }

    private void clearChunksStates() {
        realm.executeTransaction(bgRealm -> {
            RealmResults<Chunk> chunks = ChunksRealmHelper.INSTANCE.findSelectedChunksByLevelId(realm, level.getId());
            for (Chunk chunk : chunks) {
                chunk.setState(CHUNK_STATE_NORMAL);
                chunksAdapter.notifyItemChanged(chunk.getPosition());
            }
        });
    }

    private void onChunkClick(List<Chunk> chunks, int i) {
        Chunk clickedChunk = chunks.get(i);
        realm.executeTransaction(bgRealm -> updateChunkStateOnClick(clickedChunk));
        chunksAdapter.notifyItemChanged(chunks.get(i).getPosition());
        List<Chunk> selectedChunks =
                ChunksRealmHelper.INSTANCE.findSelectedChunksByLevelIdSorted(realm, level.getId());
        if (selectedChunks.size() != 0) {
            if (isWordSolved(selectedChunks)) {
                updateClearIconState(0);
                // if word was solved then check for level solved
                isLevelSolved();
            }
        }
        updateInputChunksTextView();
    }

    /**
     * Handles chunk state on click.
     *
     * @param chunk the clicked chunk.
     */
    private void updateChunkStateOnClick(Chunk chunk) {
        if (chunk.getState() == CHUNK_STATE_NORMAL) {
            chunk.setState(System.currentTimeMillis());
        } else {
            chunk.setState(CHUNK_STATE_NORMAL);
        }
    }

    private void updateInputChunksTextView() {
        List<Chunk> chunks =
                ChunksRealmHelper.INSTANCE.findSelectedChunksByLevelIdSorted(realm, level.getId());
        tvInputChunks.setText(listChunksToString(chunks));
        updateClearIconState(chunks.size());
    }

    private void updateClearIconState(int size) {
        if (size != 0) {
            imgClearIcon.setVisibility(View.VISIBLE);
        } else {
            imgClearIcon.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isWordSolved(List<Chunk> selectedChunks) {
        String solvedWordId = getSolvedWordId(level.getWords(), selectedChunks);
        // If the word was solved change its state into solved and notify adapter.
        if (!solvedWordId.equals("")) {
            realm.executeTransaction(bgRealm -> {
                Word solvedWord = WordsRealmHelper.findWordById(bgRealm, solvedWordId);
                solvedWord.setState(WORD_STATE_SOLVED);
                wordsAdapter.notifyItemChanged(solvedWord.getPosition());
            });
            removeSelectedChunks();
            return true;
        }
        return false;
    }

    private void isLevelSolved() {
        if (WordsRealmHelper.countNotSolvedWords(realm, level.getId()) == 0) {
            // Send the solved event to the Game activity
            callback.onLevelSolved();
        }
    }

    private void removeSelectedChunks() {
        realm.executeTransaction(bgRealm -> {
            List<Chunk> selectedChunks = ChunksRealmHelper.INSTANCE.findSelectedChunksByLevelId(bgRealm, level.getId());
            for (Chunk chunk : selectedChunks) {
                chunk.setState(CHUNK_STATE_GONE);
                chunksAdapter.notifyItemChanged(chunk.getPosition());
            }
        });
    }

    /**
     * Goes through the list of words to be guessed, and matches the input chunks with word chunks.
     *
     * @param words          the list of words to be solved.
     * @param selectedChunks the list of inputted chunks.
     * @return id of the solved word in the grid, and "" if none was solved.
     */
    private String getSolvedWordId(List<Word> words, List<Chunk> selectedChunks) {
        for (Word word : words) {
            boolean equals = true;
            String wordId = word.getId();
            for (Chunk selectedChunk : selectedChunks) {
                String chunkWordId = selectedChunk.getWordId();
                if (!chunkWordId.equals(wordId)) {
                    equals = false;
                    break;
                }
            }
            if (equals && word.getWord().equals(listChunksToString(selectedChunks))) {
                return word.getId();
            }
        }
        return "";
    }

    /**
     * Converts all the List of inputted chunks into the String.
     *
     * @param chunks the list of inputted chunks.
     * @return the string of inputted chunks.
     */
    private String listChunksToString(List<Chunk> chunks) {
        StringBuilder inputStr = new StringBuilder();
        for (Chunk chunk : chunks) {
            inputStr.append(chunk.getChunk());
        }
        return inputStr.toString();
    }
}