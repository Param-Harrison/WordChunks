/*
 * Copyright 2017 Julia Kozhukhovskaya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appchamp.wordchunks.ui.game.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        packColor?.let { holder.bind(words[position], it) }
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

        fun bind(word: Word, @ColorInt packColor: Int) = with(itemView) {
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
        }
    }
}