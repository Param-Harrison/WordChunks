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
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.models.realm.WORD_STATE_NOT_SOLVED
import com.appchamp.wordchunks.models.realm.WORD_STATE_SOLVED
import com.appchamp.wordchunks.models.realm.Word
import kotlinx.android.synthetic.main.item_word.view.*


class WordsAdapter(private var words: List<Word> = listOf()) :
        RecyclerView.Adapter<WordsAdapter.ViewHolder>() {

    private var levelColor: Int = Color.parseColor("#7bda7a")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(words[position], levelColor)
    }

    override fun getItemCount() = words.size

    fun updateItems(words: List<Word>) {
        this.words = words
        notifyDataSetChanged()
    }

    fun setPackColor(colorStr: String) {
        this.levelColor = Color.parseColor(colorStr)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(word: Word, @ColorInt levelColor: Int) = with(itemView) {
            val visibleLetters = word.word.take(word.visibleLettersNum)
            tvLetters.text = visibleLetters.map { it + " " }.joinToString(separator = "").dropLast(1)

            val repeatCount = word.word.length - word.visibleLettersNum
            if (repeatCount > 0) {
                tvDots.text = itemView.context.getString(R.string.dot).repeat(repeatCount)
            } else {
                tvDots.text = ""
            }

            when (word.state) {
                WORD_STATE_SOLVED -> {
                    tvLetters.background.colorFilter = PorterDuffColorFilter(
                            levelColor, PorterDuff.Mode.SRC_IN)
                    tvLetters.setTextColor(Color.parseColor("#ffffff"))
                }
                WORD_STATE_NOT_SOLVED -> {
                    tvLetters.background.colorFilter = PorterDuffColorFilter(
                            Color.parseColor("#acbbe4"), PorterDuff.Mode.SRC_IN)
                    tvLetters.setTextColor(Color.parseColor("#4c546a"))
                }
            }
        }
    }
}