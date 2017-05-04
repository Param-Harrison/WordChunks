package com.appchamp.wordchunks.game;


import com.appchamp.wordchunks.BasePresenter;
import com.appchamp.wordchunks.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface GameContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {


    }
}
