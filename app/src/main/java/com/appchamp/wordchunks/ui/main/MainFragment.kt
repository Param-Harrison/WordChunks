package com.appchamp.wordchunks.ui.main

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.data.LevelsRealmHelper
import com.appchamp.wordchunks.util.Constants.STATE_CURRENT
import io.realm.Realm
import kotlinx.android.synthetic.main.frag_main.*


class MainFragment : Fragment() {

    private var callback: OnMainFragmentClickListener? = null

    companion object {
        internal fun newInstance(): MainFragment = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater!!.inflate(R.layout.frag_main, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        imgSettingsIcon.setOnClickListener { onSettingsClick(it) }
        imgShareIcon.setOnClickListener { onShareClick(it) }
        btnPlay.setOnClickListener { onPlayClick(it) }
        btnDaily.setOnClickListener { onDailyClick(it) }
        btnPacks.setOnClickListener { onPacksClick(it) }
        btnStore.setOnClickListener { onStoreClick(it) }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = context as OnMainFragmentClickListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString()
                    + " must implement OnMainFragmentClickListener")
        }
    }

    private fun onSettingsClick(v: View?) {
        callback!!.showSlidingMenu()
    }

    private fun onShareClick(v: View) {}

    private fun onPlayClick(v: View) {
        var lastLevelId: String? = null
        Realm.getDefaultInstance().let { lastLevelId = getLastCurrentLevelId(it) }

        lastLevelId.let { callback!!.startGameActivity(lastLevelId) }
//        if (lastLevelId != null) {
//
//        } else {
//            // If all levels and packs were solved, showing the fragment
//            callback!!.showGameFinishedFragment()
//        }
    }

    private fun onPacksClick(v: View) {
        callback!!.showPacksActivity()
    }

    private fun onStoreClick(v: View) {}

    private fun onDailyClick(v: View) {}

    private fun getLastCurrentLevelId(realm: Realm): String? {

        val countLevelsByCurrentState = LevelsRealmHelper.countLevelsByState(realm, STATE_CURRENT)

        // If not all levels were solved
        if (countLevelsByCurrentState.toInt() != 0) {
            // Getting the last "current" level id
            return LevelsRealmHelper.findLastLevelByState(realm, STATE_CURRENT).id
        } else {
            // If all levels and packs were solved, showing the fragment
            return null
        }
    }

}
