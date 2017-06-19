package com.appchamp.wordchunks.ui.game

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.extensions.drawable
import com.appchamp.wordchunks.extensions.invisible
import com.appchamp.wordchunks.extensions.visible
import com.appchamp.wordchunks.realmdb.models.realm.Chunk
import com.appchamp.wordchunks.ui.game.adapters.ChunksAdapter
import com.appchamp.wordchunks.ui.game.adapters.WordsAdapter
import com.appchamp.wordchunks.util.Constants.CHUNKS_GRID_NUM
import com.appchamp.wordchunks.util.Constants.WORDS_GRID_NUM
import io.realm.RealmResults
import kotlinx.android.synthetic.main.frag_game.*


class GameFragment : LifecycleFragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(activity).get(GameViewModel::class.java)
    }

    private lateinit var wordsAdapter: WordsAdapter
    private lateinit var chunksAdapter: ChunksAdapter

    @Nullable
    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?,
                              @Nullable savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_game, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create and set the adapters for the RecyclerViews.
        setupWordsAdapter()
        setupChunksAdapter()

        // Click listeners
        imgShuffle.setOnClickListener {
            viewModel.onShuffleClick()
            chunksAdapter.notifyDataSetChanged()
        }
        imgClear.setOnClickListener {
            viewModel.onClearClick().forEach { chunksAdapter.notifyItemChanged(it) }
        }
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        subscribeUi(viewModel)
    }

    private fun subscribeUi(viewModel: GameViewModel) {
        viewModel.getLevel().observe(this, Observer {
            it?.let {
                wordsAdapter.updateItems(it.words)
                wordsAdapter.setPackColor(it.color)
                chunksAdapter.updateItems(it.chunks)
            }
        })
        viewModel.getLiveChunks().observe(this, Observer<RealmResults<Chunk>> {
            it?.let {
                updateChunksTextView(viewModel.getSelectedChunksString())
                updateChunksCountView(viewModel.getSelectedChunksLength())
                updateClearIcon(viewModel.getClearIconVisibility())
                val pos = viewModel.isWordSolved()
                if (pos != -1) {
                    wordsAdapter.notifyItemChanged(pos)
                    viewModel.onWordSolved().forEach { chunksAdapter.notifyItemChanged(it) }
                }
            }
        })
    }

    private fun setupWordsAdapter() {
        wordsAdapter = WordsAdapter()
        rvWords.adapter = wordsAdapter
        rvWords.layoutManager = CustomGridLayoutManager(activity, WORDS_GRID_NUM)
        rvWords.setHasFixedSize(true)
        rvWords.translationY = 0.5F
        rvWords.alpha = 0f
        rvWords.animate()
                .translationY(0F)
                .setDuration(1000)
                .alpha(1f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
    }

    private fun setupChunksAdapter() {
        chunksAdapter = ChunksAdapter {
            viewModel.onChunkClick(it)
            chunksAdapter.notifyItemChanged(it.position)
        }
        rvChunks.adapter = chunksAdapter
        rvChunks.layoutManager = CustomGridLayoutManager(activity, CHUNKS_GRID_NUM)
        rvChunks.setHasFixedSize(true)
        rvChunks.itemAnimator.changeDuration = 50L
    }

    private fun updateChunksTextView(text: String) {
        tvInputChunks.text = text
    }

    private fun updateChunksCountView(length: Int?) {
        var imgId = resources.getIdentifier("ic_$length", "drawable", context.packageName)
        if (imgId == 0) {
            imgId = resources.getIdentifier("ic_0", "drawable", context.packageName)
        }
        imgChunksCount.setImageDrawable(context.drawable(imgId))
        updateChunksCountView(length != 0)
    }

    private fun updateChunksCountView(isVisible: Boolean) = when {
        isVisible -> imgChunksCount.visible()
        else -> imgChunksCount.invisible()
    }

    private fun updateClearIcon(isVisible: Boolean) = when {
        isVisible -> imgClear.visible()
        else -> imgClear.invisible()
    }
}