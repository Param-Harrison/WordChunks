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
import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.realmdb.models.realm.PackState
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

    class ViewHolder<in T>(view: View, val itemClick: (T) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bind(item: T, position: Int) = with(itemView) {
            val itemState = getItemState(item)
            val itemColor = getItemColor(item)
            val drawable = imgRectBg.drawable as GradientDrawable
            rlItem.background = context.drawable(R.drawable.main_bg_rect)

            when (itemState) {
                PackState.LOCKED.value -> {
                    itemView.isEnabled = false
                    drawable.setColor(context.color(R.color.pack_rect_left_locked))
                    icon.setImageDrawable(context.drawable(R.drawable.ic_locked))
                    rlItem.background.alpha = 100

                    tvItemTitle.setTextColor(context.color(R.color.pack_title_txt_locked))
                    tvItemSubtitle.setTextColor(context.color(R.color.pack_title_txt_locked))
                }
                else -> {
                    itemView.isEnabled = true
                    drawable.setColor(itemColor)
                    tvItemTitle.setTextColor(itemColor)
                    tvItemSubtitle.setTextColor(context.color(R.color.pack_num_of_levels_txt))

                    if (itemState == PackState.FINISHED.value) {
                        icon.setImageDrawable(context.drawable(R.drawable.ic_solved))
                    } else  {
                        icon.setImageDrawable(context.drawable(R.drawable.ic_current))
                    }
                }
            }
            tvItemTitle.text = getItemTitle(item, position, resources)
            tvItemSubtitle.text = getItemSubtitle(item, resources)

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
                is Pack -> return res.getString(R.string.list_number, i + 1, item.title)
                is Level -> return res.getString(R.string.level_title, i + 1)
                else -> throw ClassCastException("Object was not of type Pack, or Level")
            }
        }

        private fun getItemSubtitle(item: T, res: Resources): String? {
            when (item) {
                is Pack -> {
                    if (getItemState(item) == PackState.LOCKED.value) {
                        return res.getString(R.string.number_of_levels_locked, item.levels.size)
                    } else {
                        val solvedLevelsCount = item.levels.count { it.state == PackState.FINISHED.value }
                        return res.getString(R.string.number_of_levels,
                                solvedLevelsCount, item.levels.size)
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