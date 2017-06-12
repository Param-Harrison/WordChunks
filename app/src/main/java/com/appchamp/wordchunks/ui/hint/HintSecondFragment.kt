package com.appchamp.wordchunks.ui.hint

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.util.Constants.WORD_ID_KEY
import kotlinx.android.synthetic.main.frag_hint_second.*
import org.jetbrains.anko.AnkoLogger


class HintSecondFragment : Fragment(), AnkoLogger {

    private lateinit var onHintSecondFragClickListener: OnHintSecondFragClickListener

    companion object {
        fun newInstance(wordId: String): HintSecondFragment {
            val args = Bundle()
            args.putString(WORD_ID_KEY, wordId)
            val hintSecondFrag: HintSecondFragment = newInstance()
            hintSecondFrag.arguments = args
            return hintSecondFrag
        }

        fun newInstance() = HintSecondFragment()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the OnHintSecondFragClickListener interface. If not, it throws an exception
        try {
            onHintSecondFragClickListener = context as OnHintSecondFragClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement OnHintSecondFragClickListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_hint_second, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvHint.text = "..."

        imgBackArrow.setOnClickListener { onHintSecondFragClickListener.onBackArrowFromHintSecondClick() }
        imgClose.setOnClickListener { onHintSecondFragClickListener.onCloseFromHintSecondClick() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null && arguments.containsKey(WORD_ID_KEY)) {

            val wordId = arguments.getString(WORD_ID_KEY)

            //val word = Word().queryFirst { it.equalTo(REALM_FIELD_ID, wordId) }

//            word?.let {
//                tvWordNumber.text = (word.position + 1).toString()
//                tvWordLetters.text = getString(R.string.number_of_letters, word.word.length)
//
//                btnFirstLetter.setOnClickListener {
//                    tvHint.text = getString(R.string.first_and_dots, word.word.take(1))
//                }
//                btnFirstChunk.setOnClickListener {
//                    //val firstChunk = Chunk().queryFirst { it.equalTo(REALM_FIELD_WORD_ID, wordId) }
//                    //tvHint.text = getString(R.string.first_and_dots, firstChunk?.chunk)
//                }
//                btnEntireSolution.setOnClickListener { tvHint.text = word.word }
//            }
        }
    }
}