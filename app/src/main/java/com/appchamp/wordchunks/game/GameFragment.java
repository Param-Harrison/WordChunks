package com.appchamp.wordchunks.game;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.realm.Chunk;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Word;

import java.util.List;

import io.realm.Realm;

import static com.appchamp.wordchunks.util.Constants.CHUNKS_GRID_NUM;
import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_NAME_ID;
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

    private List<Chunk> inputChunksList;

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

//        imgClearIcon.setOnClickListener(v -> {
//            AnimUtils.startAnimationFadeIn(getContext(), v);
//            //inputChunksList = "";
//            //tvInputChunks.setText(inputChunksList);
//        });
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        realm = Realm.getDefaultInstance();

        String levelId = getArguments().getString(EXTRA_LEVEL_ID);
        Level level = realm.where(Level.class).equalTo(REALM_FIELD_NAME_ID, levelId).findFirst();

        tvLevelClueTitle.setText(level.getClue());

        List<Word> words = level.getWords();
        wordsAdapter = new WordsAdapter(words);
        rvWords.setAdapter(wordsAdapter);
        rvWords.setLayoutManager(new GridLayoutManager(getActivity(), WORDS_GRID_NUM));

        List<Chunk> chunks = level.getChunks();
        chunksAdapter = new ChunksAdapter(chunks);
        rvChunks.setAdapter(chunksAdapter);
        rvChunks.setLayoutManager(new GridLayoutManager(getActivity(), CHUNKS_GRID_NUM));

        chunksAdapter.setOnItemClickListener((itemView, position) -> {
            realm.executeTransaction(bgRealm -> {
                if (chunks.get(position).getState() == 0) {
                    chunks.get(position).setState(1);
                }
//                else if (chunks.get(position).getState() == 1) {
//                    chunks.get(position).setState(2);
//                }
                chunksAdapter.notifyDataSetChanged();
            });
        });

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
