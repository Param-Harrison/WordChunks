package com.appchamp.wordchunks.ui.game.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.models.realm.Chunk
import kotlinx.android.synthetic.main.item_chunk.view.*


class ChunksAdapter(private val chunks: List<Chunk>, private val chunkClick: (Chunk) -> Unit) :
        RecyclerView.Adapter<ChunksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chunk, parent, false)
        return ViewHolder(view, chunkClick)
    }

    override fun getItemId(position: Int) = chunks[position].position.toLong()

    override fun onBindViewHolder(holder: ChunksAdapter.ViewHolder, i: Int) {
        holder.bind(chunks[chunks[i].position])
    }

    override fun getItemCount() = chunks.size

    class ViewHolder(view: View, val chunkClick: (Chunk) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bind(chunk: Chunk) = with(itemView) {
            val chunkState = chunk.state

            when (chunkState) {
            // Normal chunk state
                0L -> {
                    rlChunk.background.alpha = 255
                    tvChunk.alpha = 1f
                }
            // Gone state
                -1L -> {
                    rlChunk.visibility = View.INVISIBLE
                }
            // Clicked chunk state
                else -> {
                    rlChunk.background.alpha = 80
                    tvChunk.alpha = 0.2.toFloat()
                }
            }
            tvChunk.text = chunk.chunk
            itemView.setOnClickListener { chunkClick(chunk) }
        }
    }
}