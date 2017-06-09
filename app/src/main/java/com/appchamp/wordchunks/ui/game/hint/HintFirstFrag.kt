package com.appchamp.wordchunks.ui.game.hint

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.extensions.queryFirst
import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.models.realm.Word
import com.appchamp.wordchunks.ui.game.CustomGridLayoutManager
import com.appchamp.wordchunks.ui.game.adapters.WordsHintAdapter
import com.appchamp.wordchunks.ui.game.listeners.OnHintFirstFragClickListener
import com.appchamp.wordchunks.util.Constants
import io.realm.RealmList
import kotlinx.android.synthetic.main.frag_hint_first.*
import org.jetbrains.anko.AnkoLogger


class HintFirstFrag: Fragment(), AnkoLogger {

    private lateinit var onHintFirstFragClickListener: OnHintFirstFragClickListener

    private lateinit var wordsHintAdapter: WordsHintAdapter

    companion object {
        fun newInstance(levelId: String?): HintFirstFrag {
            val args = Bundle()
            args.putString(Constants.LEVEL_ID_KEY, levelId)
            val hintFirstFrag: HintFirstFrag = newInstance()
            hintFirstFrag.arguments = args
            return hintFirstFrag
        }

        fun newInstance() = HintFirstFrag()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the OnHintFirstFragClickListener interface. If not, it throws an exception
        try {
            onHintFirstFragClickListener = context as OnHintFirstFragClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement OnHintFirstFragClickListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_hint_first, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgBackArrow.setOnClickListener { onHintFirstFragClickListener.onBackArrowFromHintFirstClick() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null && arguments.containsKey(Constants.LEVEL_ID_KEY)) {

            val levelId = arguments.getString(Constants.LEVEL_ID_KEY)

            val level = Level().queryFirst { it.equalTo(Constants.REALM_FIELD_ID, levelId) }

            initWordsHintAdapter(level?.words, level?.color)
        }
    }

    private fun initWordsHintAdapter(words: RealmList<Word>?, packColor: String?) {
        wordsHintAdapter = WordsHintAdapter(words!!, Color.parseColor(packColor)) {
            onWordClick(it)
        }
        rvWordsHints.adapter = wordsHintAdapter
        rvWordsHints.layoutManager = CustomGridLayoutManager(activity, 1)
        rvWordsHints.setHasFixedSize(true)
    }

    private fun onWordClick(word: Word) {
        onHintFirstFragClickListener.onHintWordSelected(word.id)
    }


}