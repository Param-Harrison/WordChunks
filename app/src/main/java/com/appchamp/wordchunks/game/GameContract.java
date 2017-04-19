package com.appchamp.wordchunks.game;


import com.appchamp.wordchunks.BasePresenter;
import com.appchamp.wordchunks.BaseView;
import com.appchamp.wordchunks.models.Word;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface GameContract {

    interface View extends BaseView<Presenter> {

        void showWordsGrid(List<Word> words);

        void showChunksGrid(List<String> chunks);

    }

    interface Presenter extends BasePresenter {


    }
}
