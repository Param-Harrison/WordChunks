package com.appchamp.wordchunks.ui.game.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.ui.game.listeners.OnBackToLevelsListener
import com.appchamp.wordchunks.util.Constants
import kotlinx.android.synthetic.main.frag_level_solved_before.*


class LevelSolvedBeforeFrag : Fragment() {

    private var callback: OnBackToLevelsListener? = null

    companion object {
        fun newInstance(fact: String?): LevelSolvedBeforeFrag {
            val args = Bundle()
            args.putString(Constants.FACT_ID_KEY, fact)
            val levelSolvedBeforeFrag: LevelSolvedBeforeFrag = LevelSolvedBeforeFrag.newInstance()
            levelSolvedBeforeFrag.arguments = args
            return levelSolvedBeforeFrag
        }

        fun newInstance(): LevelSolvedBeforeFrag = LevelSolvedBeforeFrag()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.frag_level_solved_before, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rlBackToLevels.setOnClickListener { callback!!.onBackToLevelsSelected() }
        setFunFact()
    }

    private fun setFunFact() {
        if (arguments != null && arguments.containsKey(Constants.FACT_ID_KEY)) {
            val fact = arguments.getString(Constants.FACT_ID_KEY)
            tvFunFact.text = fact
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = context as OnBackToLevelsListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString()
                    + " must implement OnBackToLevelsListener")
        }
    }
}
