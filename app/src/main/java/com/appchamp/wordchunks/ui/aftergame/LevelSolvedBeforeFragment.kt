package com.appchamp.wordchunks.ui.aftergame

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.util.Constants
import kotlinx.android.synthetic.main.frag_level_solved_before.*


class LevelSolvedBeforeFragment : Fragment() {

//    private var callback: OnGameFragClickListener? = null

    companion object {
//        fun newInstance(fact: String?): LevelSolvedBeforeFragment {
//            val args = Bundle()
//            args.putString(Constants.FACT_ID_KEY, fact)
//            val levelSolvedBeforeFrag: LevelSolvedBeforeFragment = newInstance()
//            levelSolvedBeforeFrag.arguments = args
//            return levelSolvedBeforeFrag
//        }

        fun newInstance(): LevelSolvedBeforeFragment = LevelSolvedBeforeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_level_solved_before, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        rlBackToLevels.setOnClickListener { callback!!.onBackToLevelsClick() }
//        setFunFact()
    }

    private fun setFunFact() {
        if (arguments != null && arguments.containsKey(Constants.FACT_ID_KEY)) {
            val fact = arguments.getString(Constants.FACT_ID_KEY)
            tvFunFact.text = fact
        }
    }
}
