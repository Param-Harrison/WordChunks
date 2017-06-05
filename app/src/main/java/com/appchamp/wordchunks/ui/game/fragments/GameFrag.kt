package com.appchamp.wordchunks.ui.game.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.models.realm.Chunk
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.models.realm.Word
import com.appchamp.wordchunks.models.realm.chunksToString
import com.appchamp.wordchunks.ui.game.CustomGridLayoutManager
import com.appchamp.wordchunks.ui.game.adapters.ChunksAdapter
import com.appchamp.wordchunks.ui.game.adapters.WordsAdapter
import com.appchamp.wordchunks.ui.game.listeners.OnLevelSolvedListener
import com.appchamp.wordchunks.util.Constants
import com.appchamp.wordchunks.util.Constants.CHUNKS_GRID_NUM
import com.appchamp.wordchunks.util.Constants.CHUNK_STATE_GONE
import com.appchamp.wordchunks.util.Constants.CHUNK_STATE_NORMAL
import com.appchamp.wordchunks.util.Constants.LEVEL_ID_KEY
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_ID
import com.appchamp.wordchunks.util.Constants.WORDS_GRID_NUM
import com.appchamp.wordchunks.util.Constants.WORD_STATE_NOT_SOLVED
import com.appchamp.wordchunks.util.shuffleIntArray
import io.realm.Realm
import kotlinx.android.synthetic.main.frag_game.*
import org.jetbrains.anko.AnkoLogger


class GameFrag : Fragment(), AnkoLogger {

    private lateinit var realm: Realm
    private lateinit var chunks: List<Chunk>
    private lateinit var onLevelSolvedListener: OnLevelSolvedListener
    private lateinit var level: Level

    // Adapters
    private lateinit var wordsAdapter: WordsAdapter
    private lateinit var chunksAdapter: ChunksAdapter

    companion object {
        fun newInstance(levelId: String?): GameFrag {
            val args = Bundle()
            args.putString(LEVEL_ID_KEY, levelId)
            val gameFrag: GameFrag = newInstance()
            gameFrag.arguments = args
            return gameFrag
        }

        fun newInstance() = GameFrag()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the onLevelSolvedListener interface. If not, it throws an exception
        try {
            onLevelSolvedListener = context as OnLevelSolvedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement OnLevelSolvedListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_game, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Click listeners
        imgClear.setOnClickListener { onClearIconClick() }
        imgShuffle.setOnClickListener { onShuffleClick() }
        imgHint.setOnClickListener { onHintClick() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        realm = Realm.getDefaultInstance()

        if (arguments != null && arguments.containsKey(LEVEL_ID_KEY)) {

            val levelId = arguments.getString(LEVEL_ID_KEY)

            level = realm.where(Level::class.java)
                    .equalTo(REALM_FIELD_ID, levelId)
                    .findFirst()

            chunks = level.chunks

            tvLevelClueTitle.text = level.clue

            initWordsAdapter(level.words, Color.parseColor(level.color))
            initChunksAdapter(chunks)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Close the Realm instance.
        realm.close()
    }

    private fun initWordsAdapter(words: List<Word>, packColor: Int) {
        wordsAdapter = WordsAdapter(words, packColor)
        rvWords.adapter = wordsAdapter
        rvWords.layoutManager = CustomGridLayoutManager(activity, WORDS_GRID_NUM)
        rvWords.setHasFixedSize(true)
    }

    private fun initChunksAdapter(chunks: List<Chunk>) {
        chunksAdapter = ChunksAdapter(chunks) { onChunkClick(it) }
        chunksAdapter.setHasStableIds(true)
        rvChunks.adapter = chunksAdapter
        rvChunks.layoutManager = CustomGridLayoutManager(activity, CHUNKS_GRID_NUM)
        rvChunks.setHasFixedSize(true)
        rvChunks.itemAnimator.changeDuration = 50L
    }

    /**
     * Clicks methods.
     */
    private fun onClearIconClick() {
        realm.executeTransaction {
            chunks.filter { it.state > CHUNK_STATE_NORMAL }.map {
                it.state = CHUNK_STATE_NORMAL
                chunksAdapter.notifyItemChanged(it.position)
            }
        }
        updateChunksView()
        updateClearIcon()
    }

    private fun onShuffleClick() {
        val chunksSize = level.chunks.size
        val shuffledArray = IntArray(chunksSize, { it }).shuffleIntArray()
        val size = if (chunksSize % 2 == 0) chunksSize / 2 else chunksSize / 2 + 1
        realm.executeTransaction {
            for (i in 0..size - 1) {
                level.chunks[shuffledArray[i]].position = shuffledArray[chunksSize - i - 1]
                level.chunks[shuffledArray[chunksSize - i - 1]].position = shuffledArray[i]
            }
        }
        chunksAdapter.notifyDataSetChanged()
    }

    private fun onHintClick() {}

    private fun onChunkClick(chunk: Chunk) {
        realm.executeTransaction {
            when (chunk.state) {
                CHUNK_STATE_NORMAL -> chunk.state = System.currentTimeMillis()
                else -> chunk.state = CHUNK_STATE_NORMAL
            }
        }
        chunksAdapter.notifyItemChanged(chunk.position)
        if (isWordSolved()) {
            if (isLevelSolved()) {
                onLevelSolvedListener.onLevelSolved()
            }
        }
        updateChunksView()
        updateClearIcon()
    }

    private fun updateChunksView() {
        tvInputChunks?.text = getSelectedChunks()
    }

    private fun updateClearIcon() {
        return when {
            chunks.filter { it.state > CHUNK_STATE_NORMAL }.isEmpty() -> imgClear?.visibility = View.GONE
            else -> imgClear?.visibility = View.VISIBLE
        }
    }

    private fun isWordSolved(): Boolean {
        val selectedChunks = getSelectedChunks()

        level.words
                .filter { it.state == WORD_STATE_NOT_SOLVED }
                .forEach {
                    if (it.word == selectedChunks) {
                        removeChunks()
                        changeWordState(it)
                        return true
                    }
                }
        return false
    }

    private fun changeWordState(word: Word) {
        realm.executeTransaction {
            word.state = Constants.WORD_STATE_SOLVED
            wordsAdapter.notifyItemChanged(word.position)
        }
    }

    private fun getSelectedChunks(): String = chunks
            .filter { it.state > CHUNK_STATE_NORMAL }
            .sortedBy { it.state }
            .chunksToString()

    private fun removeChunks() {
        realm.executeTransaction {
            chunks.filter { it.state > CHUNK_STATE_NORMAL }.map {
                it.state = CHUNK_STATE_GONE
                chunksAdapter.notifyItemChanged(it.position)
            }
        }
    }

    private fun isLevelSolved(): Boolean {
        if (level.words.filter { it.state == WORD_STATE_NOT_SOLVED }.isEmpty()) {
            resetLevel()
            return true
        }
        return false
    }

    /**
     * Resets all level data
     */
    private fun resetLevel() {
        realm.executeTransaction {
            level.words.forEach { it.state = WORD_STATE_NOT_SOLVED }
            level.chunks.forEach { it.state = CHUNK_STATE_NORMAL }
        }
    }
}
