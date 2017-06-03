package com.appchamp.wordchunks.ui.game.fragments

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.ui.game.listeners.OnNextLevelListener
import com.appchamp.wordchunks.util.Constants.CLUE_ID_KEY
import com.appchamp.wordchunks.util.Constants.COLOR_ID_KEY
import com.appchamp.wordchunks.util.Constants.FACT_ID_KEY
import com.appchamp.wordchunks.util.Constants.LEFT_ID_KEY
import kotlinx.android.synthetic.main.frag_level_solved.*
import java.util.*


class LevelSolvedFrag : Fragment() {

    private var callback: OnNextLevelListener? = null

    companion object {

        fun newInstance(color: Int, clue: String, fact: String, left: Long): LevelSolvedFrag {
            val args = Bundle()
            args.putInt(COLOR_ID_KEY, color)
            args.putString(CLUE_ID_KEY, clue)
            args.putString(FACT_ID_KEY, fact)
            args.putLong(LEFT_ID_KEY, left)
            val levelSolvedFrag: LevelSolvedFrag = newInstance()
            levelSolvedFrag.arguments = args
            return levelSolvedFrag
        }

        fun newInstance(): LevelSolvedFrag = LevelSolvedFrag()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.frag_level_solved, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rlNextLevel.setOnClickListener { callback!!.onNextLevelSelected() }

        setPackColor()
        setClue()
        setFunFact()
        setLevelsLeft()
        setExcellent()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = context as OnNextLevelListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString() + " must implement OnNextLevelListener")
        }

    }

    private fun setPackColor() {
        val color = arguments.getInt(COLOR_ID_KEY)
        val drawable = imgRectBg.drawable as GradientDrawable
        drawable.setColor(color)
        tvNextLevelTitle.setTextColor(color)
    }

    private fun setClue() {
        val clue = arguments.getString(CLUE_ID_KEY)
        if (clue === "") {
            tvNextLevelClue.text = "CONGRATULATIONS!"
        } else {
            tvNextLevelClue.text = clue
        }
    }

    private fun setFunFact() {
        val fact = arguments.getString(FACT_ID_KEY)
        tvFunFact.text = fact
    }

    private fun setLevelsLeft() {
        val left = arguments.getLong(LEFT_ID_KEY)
        if (left.toInt() == 0) {
            tvLevelsLeft.text = "YOU'VE FINISHED THE WHOLE PACK!"
        } else if (left.toInt() == 1) {
            tvLevelsLeft.text = "ONLY ONE LEVEL LEFT IN PACK"
        } else {
            tvLevelsLeft.text = left.toString() + " LEVELS LEFT IN PACK"
        }
    }

    private fun setExcellent() {
        val res = context.resources
        val congrats = res.getStringArray(R.array.congrats)
        val i = Random().nextInt(congrats.size - 1)
        tvExcellent.text = congrats[i]
    }
}
