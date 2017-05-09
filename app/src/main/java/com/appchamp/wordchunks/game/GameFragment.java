package com.appchamp.wordchunks.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.DatabaseHelperRealm;
import com.appchamp.wordchunks.models.realm.Chunk;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Word;
import com.appchamp.wordchunks.util.AnimUtils;
import com.appchamp.wordchunks.util.RealmUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.appchamp.wordchunks.util.Constants.CHUNKS_GRID_NUM;
import static com.appchamp.wordchunks.util.Constants.CHUNK_STATE_GONE;
import static com.appchamp.wordchunks.util.Constants.CHUNK_STATE_NORMAL;
import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.NUMBER_OF_CHUNKS;
import static com.appchamp.wordchunks.util.Constants.WORDS_GRID_NUM;
import static com.appchamp.wordchunks.util.Constants.WORD_STATE_SOLVED;


public class GameFragment extends Fragment {//implements GameContract.View {

    private Realm realm;
    private DatabaseHelperRealm db;
    //private GameContract.Presenter presenter;

    private RecyclerView rvWords;
    private RecyclerView rvChunks;
    private TextView tvLevelClueTitle;
    private TextView tvInputChunks;
    private ImageView imgClearIcon;
    private Button btnSend;
    private Button btnShuffle;

    private WordsAdapter wordsAdapter;
    private ChunksAdapter chunksAdapter;

    private String levelId;
    private List<Word> words;
    private List<Chunk> chunks;

    public GameFragment() {
        // Requires empty public constructor
    }

    public static GameFragment newInstance() {
        return new GameFragment();
    }

//    @Override
//    public void setPresenter(@NonNull GameContract.Presenter presenter) {
//        this.presenter = presenter;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game, container, false);
        // Setup initial views
        tvLevelClueTitle = (TextView) root.findViewById(R.id.tvLevelClueTitle);
        rvWords = (RecyclerView) root.findViewById(R.id.rvWords);
        rvChunks = (RecyclerView) root.findViewById(R.id.rvChunks);
        tvInputChunks = (TextView) root.findViewById(R.id.tvInputChunks);
        imgClearIcon = (ImageView) root.findViewById(R.id.imgClearIcon);
        btnSend = (Button) root.findViewById(R.id.btnSend);
        btnShuffle = (Button) root.findViewById(R.id.btnShuffle);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        realm = Realm.getDefaultInstance();
        db = new DatabaseHelperRealm();

        levelId = getArguments().getString(EXTRA_LEVEL_ID);
        Level level = db.findLevelById(realm, levelId);

        initLevelClueTitle(level.getClue());
        words = level.getWords();
        initWordsAdapter(words);
        chunks = level.getChunks();
        initChunksAdapter(chunks);
        updateInputChunksTextView();

        // Clicks methods
        imgClearIcon.setOnClickListener(this::onClearIconClick);
        btnSend.setOnClickListener(this::onSendClick);
        btnShuffle.setOnClickListener(this::onShuffleClick);
        btnShuffle.callOnClick();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        presenter.start();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    private void initLevelClueTitle(String clue) {
        tvLevelClueTitle.setText(clue);
    }

    private void initWordsAdapter(List<Word> words) {
        wordsAdapter = new WordsAdapter(words);
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
        int[] a = RealmUtils.shuffleArray();
        realm.executeTransaction(bgRealm -> {
            for (int i = 0; i < NUMBER_OF_CHUNKS / 2; i++) {
                chunks.get(a[i]).setPosition(a[NUMBER_OF_CHUNKS - i - 1]);
                chunks.get(a[NUMBER_OF_CHUNKS - i - 1]).setPosition(a[i]);
            }
        });
        chunksAdapter.notifyDataSetChanged();
    }

    private void clearChunksStates() {
        realm.executeTransaction(bgRealm -> {
            RealmResults<Chunk> chunks = db.findSelectedChunksByLevelId(realm, levelId);
            for (Chunk chunk : chunks) {
                chunk.setState(CHUNK_STATE_NORMAL);
                chunksAdapter.notifyItemChanged(chunk.getPosition());
            }
        });
    }

    private void onChunkClick(List<Chunk> chunks, int i) {
        Chunk clickedChunk = chunks.get(i);
        realm.executeTransaction(bgRealm -> changeChunkStateOnClick(clickedChunk));
        chunksAdapter.notifyItemChanged(chunks.get(i).getPosition());
        updateInputChunksTextView();
    }

    /**
     * Handles chunk state on click.
     *
     * @param chunk the clicked chunk.
     */
    private void changeChunkStateOnClick(Chunk chunk) {
        if (chunk.getState() == CHUNK_STATE_NORMAL) {
            chunk.setState(System.currentTimeMillis());
        } else {
            chunk.setState(CHUNK_STATE_NORMAL);
        }
    }

    private void updateInputChunksTextView() {
        List<Chunk> chunks = db.findSelectedChunksByLevelIdSorted(realm, levelId);
        tvInputChunks.setText(listChunksToString(chunks));
    }

    private void onSendClick(View v) {
        List<Chunk> selectedChunks = db.findSelectedChunksByLevelIdSorted(realm, levelId);
        if (selectedChunks.size() != 0) {
            String solvedWordId = getSolvedWordId(words, selectedChunks);
            if (!solvedWordId.equals("")) {
                realm.executeTransaction(bgRealm -> {
                    Word solvedWord = db.findWordById(bgRealm, solvedWordId);
                    solvedWord.setState(WORD_STATE_SOLVED);
                    wordsAdapter.notifyItemChanged(solvedWord.getPosition());
                });
                removeSelectedChunks();
            } else {
                clearChunksStates();
            }
            updateInputChunksTextView();
        } else {
            Toast.makeText(getActivity(), getString(R.string.send_hint_text),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void removeSelectedChunks() {
        realm.executeTransaction(bgRealm -> {
            List<Chunk> selectedChunks = db.findSelectedChunksByLevelId(bgRealm, levelId);
            for (Chunk chunk : selectedChunks) {
                chunk.setState(CHUNK_STATE_GONE);
                chunksAdapter.notifyItemChanged(chunk.getPosition());
            }
        });
    }

    /**
     * Goes through the list of words to be guessed, and matches the input chunks with word chunks.
     *
     * @param words the list of words to be solved.
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