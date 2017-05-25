package com.appchamp.wordchunks.ui.game;

import com.appchamp.wordchunks.ui.BasePresenter;
import com.appchamp.wordchunks.ui.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface GameContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {


    }
}
