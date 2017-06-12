package com.appchamp.wordchunks.ui.main

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import kotlinx.android.synthetic.main.frag_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.share


class MainFragment : Fragment(), AnkoLogger {

    private lateinit var onMainFragmentClickListener: OnMainFragmentClickListener

    companion object {
        internal fun newInstance() = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgSettingsIcon.setOnClickListener { onSettingsClick() }
        imgShareIcon.setOnClickListener { onShareClick() }
        btnPlay.setOnClickListener { onPlayClick() }
        btnDaily.setOnClickListener { onDailyClick() }
        btnPacks.setOnClickListener { onPacksClick() }
        btnStore.setOnClickListener { onStoreClick() }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the onMainFragmentClickListener interface. If not, it throws an exception
        try {
            onMainFragmentClickListener = context as OnMainFragmentClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement OnMainFragmentClickListener")
        }
    }

    private fun onSettingsClick() = onMainFragmentClickListener.showSlidingMenu()

    private fun onShareClick() {
        context.share(
                "WordChunks is AWESOME and I think you'll love it. Get it! -- [link]",
                "My current puzzle")
    }

    private fun onPlayClick() {
        //val levelId: String? = getLastCurrentLevelId()
        //info { levelId }
//        if (levelId != null) {
//            onMainFragmentClickListener.startGameActivity(levelId)
//        } else {
//            // If all levels and packs were solved, showing the fragment
//            onMainFragmentClickListener.showGameFinishedFragment()
//        }
    }

    private fun onPacksClick() = onMainFragmentClickListener.startPacksActivity()

    private fun onStoreClick() {}

    private fun onDailyClick() {}

//    private fun getLastCurrentLevelId(): String? =
//            Level().queryLast { it.equalTo(REALM_FIELD_STATE, STATE_CURRENT) }?.id
}
