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

package com.appchamp.wordchunks.ui.packs

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.extensions.color
import com.appchamp.wordchunks.extensions.drawable
import com.appchamp.wordchunks.extensions.gone
import com.appchamp.wordchunks.extensions.visible
import com.appchamp.wordchunks.models.realm.FINISHED
import com.appchamp.wordchunks.models.realm.LOCKED
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.models.realm.Pack
import com.appchamp.wordchunks.realmdb.utils.levelModel
import io.realm.Realm
import kotlinx.android.synthetic.main.item_pack_level.view.*


class PacksLevelsAdapter<T>(private var items: List<T> = listOf(),
                            private val itemClick: (T) -> Unit) :
        RecyclerView.Adapter<PacksLevelsAdapter.ViewHolder<T>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pack_level, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount() = items.size

    fun updateItems(items: List<T>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder<in T>(view: View, private val itemClick: (T) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bind(item: T, position: Int) = with(itemView) {
            val itemState = getItemState(item)
            val itemColor = getItemColor(item)
            val drawable = imgRectBg.drawable as GradientDrawable
            rlItem.background = context.drawable(R.drawable.shape_btn_main)


            tvItemTitle.text = getItemTitle(item, position, resources)
            when (item) {
                is Pack -> {
                    tvItemNumber.visible()
                    tvItemNumber.setTextColor(Color.parseColor("#7d8cb6"))
                    tvItemNumber.text = (item.number + 1).toString() + "."
                    tvItemTitle.setTextColor(Color.parseColor("#e2e2e2"))
                    tvItemSubtitle.setTextColor(itemColor)
                }
                is Level -> {
                    tvItemNumber.gone()
                    tvItemTitle.setTextColor(itemColor)
                    tvItemSubtitle.setTextColor(Color.parseColor("#7d8cb6"))
                }
            }
            tvItemSubtitle.text = getItemSubtitle(item, resources)
            when (itemState) {
                LOCKED -> {
                    itemView.isEnabled = false
                    drawable.setColor(context.color(R.color.pack_rect_left_locked))
                    icon.setImageDrawable(context.drawable(R.drawable.ic_locked))
                    rlItem.background.alpha = 100
                    tvItemNumber.setTextColor(context.color(R.color.pack_title_txt_locked))
                    tvItemTitle.setTextColor(context.color(R.color.pack_title_txt_locked))
                    tvItemSubtitle.setTextColor(context.color(R.color.pack_title_txt_locked))
                }
                else -> {
                    itemView.isEnabled = true
                    drawable.setColor(itemColor)
                    if (itemState == FINISHED) {
                        icon.setImageDrawable(context.drawable(R.drawable.ic_solved))
                    } else {
                        icon.setImageDrawable(context.drawable(R.drawable.ic_current))
                    }
                }
            }

            itemView.setOnClickListener { itemClick(item) }
        }

        private fun getItemColor(item: T): Int {
            when (item) {
                is Pack -> return Color.parseColor(item.color)
                is Level -> return Color.parseColor(item.color)
                else -> throw ClassCastException("Object was not of type Pack, or Level")
            }
        }

        private fun getItemTitle(item: T, i: Int, res: Resources): String {
            when (item) {
                is Pack -> return item.title
                is Level -> return res.getString(R.string.level_title, i + 1)
                else -> throw ClassCastException("Object was not of type Pack, or Level")
            }
        }

        private fun getItemSubtitle(item: T, res: Resources): String? {
            when (item) {
                is Pack -> {
                    var numberOfLevels = 0
                    var numberOfSolvedLevels = 0
                    Realm.getDefaultInstance().use {
                        val levels = it.levelModel().findLevelsByPackIdList(item.id)
                        numberOfLevels = levels.size
                        numberOfSolvedLevels = levels.count { it.state == FINISHED }
                    }
                    if (getItemState(item) == LOCKED) {
                        return res.getString(R.string.number_of_levels_locked, numberOfLevels)
                    } else {
                        return res.getString(R.string.number_of_levels, numberOfSolvedLevels, numberOfLevels)
                    }
                }
                is Level -> return item.clue
                else -> throw ClassCastException("Object was not of type Pack, or Level")
            }
        }

        private fun getItemState(item: T): Int {
            when (item) {
                is Pack -> return item.state
                is Level -> return item.state
                else -> throw ClassCastException("Object was not of type Pack, or Level")
            }
        }
    }
}