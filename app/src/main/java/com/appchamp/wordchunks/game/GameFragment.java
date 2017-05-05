package com.appchamp.wordchunks.game;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.realm.Chunk;
import com.appchamp.wordchunks.models.realm.ChunkInput;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Word;
import com.appchamp.wordchunks.util.AnimUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.appchamp.wordchunks.util.Constants.CHUNKS_GRID_NUM;
import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_NAME_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_NAME_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_NAME_POSITION_IN_GRID;
import static com.appchamp.wordchunks.util.Constants.WORDS_GRID_NUM;


public class GameFragment extends Fragment implements GameContract.View {

    private Realm realm;

    private GameContract.Presenter presenter;

    private WordsAdapter wordsAdapter;
    private ChunksAdapter chunksAdapter;

    private RecyclerView rvWords;
    private RecyclerView rvChunks;
    private TextView tvLevelClueTitle;
    private TextView tvInputChunks;
    private ImageView imgClearIcon;

    private List<ChunkInput> inputChunksList;

    private String levelId;

    public GameFragment() {
        // Requires empty public constructor
    }

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void setPresenter(@NonNull GameContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game, container, false);

        tvLevelClueTitle = (TextView) root.findViewById(R.id.tvLevelClueTitle);
        rvWords = (RecyclerView) root.findViewById(R.id.rvWords);
        rvChunks = (RecyclerView) root.findViewById(R.id.rvChunks);
        tvInputChunks = (TextView) root.findViewById(R.id.tvInputChunks);
        imgClearIcon = (ImageView) root.findViewById(R.id.imgClearIcon);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        realm = Realm.getDefaultInstance();

        levelId = getArguments().getString(EXTRA_LEVEL_ID);
        Level level = realm.where(Level.class).equalTo(REALM_FIELD_NAME_ID, levelId).findFirst();

        tvLevelClueTitle.setText(level.getClue());
        inputChunksList = level.getInputChunks();
        updateInputChunksTextView();

        imgClearIcon.setOnClickListener(v -> {
            AnimUtils.startAnimationFadeIn(getContext(), v);
            deleteAllInputChunksFromList();
            updateInputChunksTextView();
        });

        List<Word> words = level.getWords();
        wordsAdapter = new WordsAdapter(words);
        rvWords.setAdapter(wordsAdapter);
        rvWords.setLayoutManager(new CustomGridLayoutManager(getActivity(), WORDS_GRID_NUM));
        rvWords.setHasFixedSize(true);

        List<Chunk> chunks = level.getChunks();
        chunksAdapter = new ChunksAdapter(chunks);
        rvChunks.setAdapter(chunksAdapter);
        rvChunks.setLayoutManager(new CustomGridLayoutManager(getActivity(), CHUNKS_GRID_NUM));
        //rvChunks.addItemDecoration(new VerticalGridCardSpacingDecoration());
        rvChunks.setHasFixedSize(true);

        chunksAdapter.setOnItemClickListener((itemView, position) ->
                realm.executeTransaction(bgRealm -> {
                    changeChunkState(bgRealm, chunks, position);
                    chunksAdapter.notifyItemChanged(position);
                    updateInputChunksTextView();
                }));
    }

    private void updateInputChunksTextView() {
        tvInputChunks.setText(convertChunksToString(inputChunksList));
    }

    private void addInInputChunksList(Realm realm, List<Chunk> chunks, int position) {
        ChunkInput addedInputChunk = realm.createObject(ChunkInput.class);
        addedInputChunk.setChunk(chunks.get(position).getChunk());
        addedInputChunk.setLevelId(levelId);
        addedInputChunk.setPositionInGrid(position);
        inputChunksList.add(addedInputChunk);
    }

    private void changeChunkState(Realm realm, List<Chunk> chunks, int position) {
        if (chunks.get(position).getState() == 0) {
            addInInputChunksList(realm, chunks, position);
            chunks.get(position).setState(1);
        } else if (chunks.get(position).getState() == 1) {
            deleteInputChunkFromList(realm, position);
            chunks.get(position).setState(0);
        }
    }

    private void deleteInputChunkFromList(Realm realm, int position) {
        ChunkInput inputChunks = realm.where(ChunkInput.class)
                .equalTo(REALM_FIELD_NAME_POSITION_IN_GRID, position)
                .equalTo(REALM_FIELD_NAME_LEVEL_ID, levelId)
                .findFirst();
        inputChunks.deleteFromRealm();
    }

    /**
     * Deletes in Realm all inputted chunks after clear icon was clicked.
     */
    private void deleteAllInputChunksFromList() {
        realm.executeTransaction(bgRealm -> {
            Level level = bgRealm
                    .where(Level.class)
                    .equalTo(REALM_FIELD_NAME_ID, levelId)
                    .findFirst();

            List<Chunk> chunks = level.getChunks();
            RealmResults<ChunkInput> inputChunks = bgRealm
                    .where(ChunkInput.class)
                    .equalTo(REALM_FIELD_NAME_LEVEL_ID, levelId)
                    .findAll();
            for (ChunkInput chunkInput : inputChunks) {
                int chunkPosition = chunkInput.getPositionInGrid();
                chunks.get(chunkPosition).setState(0);
                chunksAdapter.notifyItemChanged(chunkPosition);
            }
            inputChunks.deleteAllFromRealm();
        });
    }

    private String convertChunksToString(List<ChunkInput> inputChunks) {
        StringBuilder inputStr = new StringBuilder();
        for (ChunkInput chunk : inputChunks) {
            inputStr.append(chunk.getChunk());
        }
        return inputStr.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }
}
