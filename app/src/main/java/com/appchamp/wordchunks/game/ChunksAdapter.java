package com.appchamp.wordchunks.game;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.realm.Chunk;

import java.util.List;


public class ChunksAdapter extends RecyclerView.Adapter<ChunksAdapter.ViewHolder> {

    private List<Chunk> chunks;
    private ChunksAdapter.OnItemClickListener listener;

    ChunksAdapter(List<Chunk> chunks) {
        setChunks(chunks);
    }

    public void updateChunks(List<Chunk> chunks) {
        setChunks(chunks);
        notifyDataSetChanged();
    }

    private void setChunks(List<Chunk> chunks) {
        if (chunks != null) {
            this.chunks = chunks;
        } else {
            Log.e("ChunksAdapter", "chunks cannot be null");
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(ChunksAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ChunksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View levelView = inflater.inflate(R.layout.item_chunk, parent, false);

        return new ChunksAdapter.ViewHolder(levelView);
    }

    @Override
    public void onBindViewHolder(ChunksAdapter.ViewHolder holder, int i) {
        final Chunk chunk = getItem(i);
        final int chunkState = chunk.getState();

        // normal state, not clicked yet
        if (chunkState == 0) {
            holder.rlChunk.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.white));
        }
        // clicked state
        else if (chunkState == 1) {
            holder.rlChunk.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.white_30_percent));
        }
        // invisible state
//        else if (chunkState == 2) {
//            holder.itemView.setVisibility(View.INVISIBLE);
//        }

        holder.tvChunk.setText(chunk.getChunk());
    }

    @Override
    public int getItemCount() {
        return chunks.size();
    }

    private Chunk getItem(int i) {
        return chunks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlChunk;
        TextView tvChunk;

        ViewHolder(View itemView) {
            super(itemView);

            rlChunk = (RelativeLayout) itemView.findViewById(R.id.rlChunk);
            tvChunk = (TextView) itemView.findViewById(R.id.tvChunk);

            // Setup the click listener
            itemView.setOnClickListener(v -> {
                // Triggers click upwards to the adapter on click
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }
}