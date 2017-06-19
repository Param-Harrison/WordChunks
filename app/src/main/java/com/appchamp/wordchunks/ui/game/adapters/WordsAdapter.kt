package com.appchamp.wordchunks.ui.game.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.extensions.color
import com.appchamp.wordchunks.extensions.gone
import com.appchamp.wordchunks.extensions.visible
import com.appchamp.wordchunks.realmdb.models.realm.Word
import com.appchamp.wordchunks.realmdb.models.realm.WordState
import kotlinx.android.synthetic.main.item_word.view.*


class WordsAdapter(private var words: List<Word> = listOf()) :
        RecyclerView.Adapter<WordsAdapter.ViewHolder>() {

    var packColor: Int? = Color.parseColor("#cccccc")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        packColor?.let { holder.bind(words[position], it, position) }
    }

    override fun getItemCount() = words.size

    fun updateItems(words: List<Word>) {
        this.words = words
        notifyDataSetChanged()
    }

    fun setPackColor(colorStr: String) {
        this.packColor = Color.parseColor(colorStr)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(word: Word, @ColorInt packColor: Int, position: Int) = with(itemView) {
            val wordState = word.state
            val drawable = imgRectBg.drawable as GradientDrawable
            when (wordState) {
                WordState.NOT_SOLVED.value -> {
                    val wordLength = word.word.length
                    tvWord.text = resources.getString(R.string.number_of_letters, wordLength)
                    tvWordNum.text = word.getProperIndex()
                    drawable.setColor(context.color(R.color.word_rect_bg))
                }
                else -> {
                    tvWord.text = word.word
                    tvWord.setTextColor(packColor)
                    icon.visible()
                    tvWordNum.gone()
                    drawable.setColor(packColor)
                }
            }
            when (position) {
            // If right column
                1, 3, 5 -> setItemLayout(RelativeLayout.ALIGN_PARENT_RIGHT, Gravity.START)
            // If left column
                else -> setItemLayout(RelativeLayout.ALIGN_PARENT_LEFT, Gravity.END)
            }
        }

        private fun setItemLayout(alignParent: Int, gravity: Int) {
            var params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT)

            params.addRule(alignParent, RelativeLayout.TRUE)

            itemView.icon.layoutParams = params
            itemView.imgRectBg.layoutParams = params
            itemView.tvWord.gravity = gravity or Gravity.CENTER_VERTICAL

            params = RelativeLayout.LayoutParams(
                    TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            32f,
                            itemView.resources.displayMetrics).toInt(),
                    RelativeLayout.LayoutParams.MATCH_PARENT)

            params.addRule(alignParent, RelativeLayout.TRUE)
            itemView.tvWordNum.layoutParams = params
        }
    }
}