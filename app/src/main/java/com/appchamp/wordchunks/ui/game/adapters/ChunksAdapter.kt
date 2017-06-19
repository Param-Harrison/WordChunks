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

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.extensions.invisible
import com.appchamp.wordchunks.realmdb.models.realm.Chunk
import com.appchamp.wordchunks.realmdb.models.realm.ChunkState
import kotlinx.android.synthetic.main.item_chunk.view.*
import java.util.*




class ChunksAdapter(private var chunks: List<Chunk> = listOf(),
                    private val chunkClick: (Chunk) -> Unit) :
        RecyclerView.Adapter<ChunksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chunk, parent, false)
        return ViewHolder(view, chunkClick)
    }

    override fun onBindViewHolder(holder: ChunksAdapter.ViewHolder, i: Int) {
        // To prevent appearance of the gone chunks on start
        if (chunks[chunks[i].position].state != ChunkState.GONE.value) {
            setAnimation(holder.itemView, i)
        }

        holder.bind(chunks[chunks[i].position])
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = chunks[position].position.toLong()

    override fun getItemCount() = chunks.size

    fun updateItems(chunks: List<Chunk>) {
        this.chunks = chunks
        notifyDataSetChanged()
    }

    class ViewHolder(view: View, val chunkClick: (Chunk) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bind(chunk: Chunk) = with(itemView) {
            val chunkState = chunk.state

            when (chunkState) {
            // Normal chunk state
                ChunkState.NORMAL.value -> {
                    rlChunk.background.alpha = 255
                    tvChunk.alpha = 1f
                }
            // Gone state
                ChunkState.GONE.value -> {
                    rlChunk.invisible()
                }
            // Clicked chunk state
                else -> {
                    rlChunk.background.alpha = 80
                    tvChunk.alpha = 0.2f
                }
            }
            tvChunk.text = chunk.chunk
            itemView.setOnClickListener { chunkClick(chunk) }
        }
    }

    private var mLastPosition = -1

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > mLastPosition) {
            val anim = ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            // to make duration random number between [0,501)
            anim.duration = Random().nextInt(1000).toLong() + 500
            viewToAnimate.startAnimation(anim)
            mLastPosition = position
        }
    }
}