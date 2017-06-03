package com.appchamp.wordchunks.ui.game.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.data.ChunksRealmHelper
import com.appchamp.wordchunks.data.LevelsRealmHelper
import com.appchamp.wordchunks.data.PacksRealmHelper
import com.appchamp.wordchunks.data.WordsRealmHelper
import com.appchamp.wordchunks.models.realm.Chunk
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.models.realm.Word
import com.appchamp.wordchunks.models.realm.chunksToString
import com.appchamp.wordchunks.ui.game.CustomGridLayoutManager
import com.appchamp.wordchunks.ui.game.adapters.ChunksAdapter
import com.appchamp.wordchunks.ui.game.adapters.WordsAdapter
import com.appchamp.wordchunks.ui.game.listeners.OnLevelSolvedListener
import com.appchamp.wordchunks.util.Constants.CHUNKS_GRID_NUM
import com.appchamp.wordchunks.util.Constants.CHUNK_STATE_GONE
import com.appchamp.wordchunks.util.Constants.CHUNK_STATE_NORMAL
import com.appchamp.wordchunks.util.Constants.LEVEL_ID_KEY
import com.appchamp.wordchunks.util.Constants.WORDS_GRID_NUM
import com.appchamp.wordchunks.util.Constants.WORD_STATE_SOLVED
import com.appchamp.wordchunks.util.shuffleIntArray
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.frag_game.*


class GameFrag : Fragment() {

    val realm: Realm = Realm.getDefaultInstance()
    private var callback: OnLevelSolvedListener? = null
    private var level: Level? = null

    // Adapters
    private var wordsAdapter: WordsAdapter? = null
    private var chunksAdapter: ChunksAdapter? = null

    companion object {
        fun newInstance(levelId: String?): GameFrag {
            val args = Bundle()
            args.putString(LEVEL_ID_KEY, levelId)
            val gameFrag: GameFrag = newInstance()
            gameFrag.arguments = args
            return gameFrag
        }

        fun newInstance(): GameFrag = GameFrag()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = context as OnLevelSolvedListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString() + " must implement OnLevelSolvedListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.frag_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null && arguments.containsKey(LEVEL_ID_KEY)) {

            val levelId = arguments.getString(LEVEL_ID_KEY)

            level = LevelsRealmHelper.findLevelById(realm, levelId!!)

            initLevelClueTitle(level!!.clue)

            val packColor = Color.parseColor(
                    PacksRealmHelper.findFirstPackById(realm, level!!.packId!!)
                            .color)

            initWordsAdapter(level!!.words, packColor)
            initChunksAdapter(level!!.chunks)

            updateInputChunksTextView()

            // Sets click listeners
            imgClearIcon.setOnClickListener(this::onClearIconClick)
            //btnSend.setOnClickListener(this::isWordSolved);
            imgShuffle.setOnClickListener(this::onShuffleClick)
            imgHint.setOnClickListener(this::onHintClick)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() // Remember to close Realm when done.
    }

    private fun initLevelClueTitle(clue: String?) {
        tvLevelClueTitle.text = clue
    }

    private fun initWordsAdapter(words: RealmList<Word>?, packColor: Int) {
        wordsAdapter = WordsAdapter(words, packColor)
        rvWords!!.adapter = wordsAdapter
        rvWords!!.layoutManager = CustomGridLayoutManager(activity, WORDS_GRID_NUM)
        rvWords!!.setHasFixedSize(true)
    }

    private fun initChunksAdapter(chunks: RealmList<Chunk>?) {
        chunksAdapter = ChunksAdapter(chunks)
        chunksAdapter!!.setHasStableIds(true)
        rvChunks!!.adapter = chunksAdapter
        rvChunks!!.layoutManager = CustomGridLayoutManager(activity, CHUNKS_GRID_NUM)
        rvChunks!!.setHasFixedSize(true)
        chunksAdapter!!.setOnItemClickListener { _, pos -> onChunkClick(chunks, pos) }
    }

    /**
     * Clicks methods.
     */
    private fun onClearIconClick(v: View) {
        val animFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        v.startAnimation(animFadeIn)
        clearChunksStates()
        updateInputChunksTextView()
    }

    private fun onShuffleClick(v: View) {
        val chunksSize = level!!.chunks!!.size
        val shuffledArray = IntArray(chunksSize, { it }).shuffleIntArray()
        val size = if (chunksSize % 2 == 0) chunksSize / 2 - 1 else chunksSize / 2
        realm.executeTransaction {
            for (i in 0..size) {
                level!!.chunks!![shuffledArray[i]].position = shuffledArray[chunksSize - i - 1]
                level!!.chunks!![shuffledArray[chunksSize - i - 1]].position = shuffledArray[i]
            }
        }
        chunksAdapter!!.notifyDataSetChanged()
    }

    private fun onHintClick(v: View) {
        TODO()
    }

    private fun clearChunksStates() {
        realm.executeTransaction {
            val chunks = ChunksRealmHelper.findSelectedChunksByLevelId(it!!, level!!.id!!)
            for (chunk in chunks) {
                chunk.state = CHUNK_STATE_NORMAL.toLong()
                chunksAdapter!!.notifyItemChanged(chunk.position)
            }
        }
    }

    private fun onChunkClick(chunks: RealmList<Chunk>?, i: Int) {
        val clickedChunk = chunks?.get(i)
        realm.executeTransaction { updateChunkStateOnClick(clickedChunk) }
        chunksAdapter!!.notifyItemChanged(chunks?.get(i)?.position as Int)
        val selectedChunks = ChunksRealmHelper.findSelectedChunksByLevelIdSorted(realm, level!!.id!!)
        if (selectedChunks.isNotEmpty()) {
            if (isWordSolved(selectedChunks)) {
                updateClearIconState(0)
                // if word was solved then check for level solved
                isLevelSolved()
            }
        }
        updateInputChunksTextView()
    }

    /**
     * Handles chunk state on click.
     *
     * @param chunk the clicked chunk.
     */
    private fun updateChunkStateOnClick(chunk: Chunk?) {
        when (chunk?.state) {
            CHUNK_STATE_NORMAL.toLong() -> chunk.state = System.currentTimeMillis()
            else -> chunk?.state = CHUNK_STATE_NORMAL.toLong()
        }
    }

    private fun updateInputChunksTextView() {
        val chunks = ChunksRealmHelper.findSelectedChunksByLevelIdSorted(realm, level!!.id!!)
        tvInputChunks.text = chunks.chunksToString()
        updateClearIconState(chunks.size)
    }

    private fun updateClearIconState(size: Int) = when {
        size != 0 -> imgClearIcon.visibility = View.VISIBLE
        else -> imgClearIcon.visibility = View.INVISIBLE
    }

    private fun isWordSolved(selectedChunks: List<Chunk>): Boolean {
        val solvedWordId = getSolvedWordId(level!!.words, selectedChunks)
        // If the word was solved change its state into solved and notify adapter.
        if (solvedWordId != "") {
            realm.executeTransaction { bgRealm ->
                val solvedWord = WordsRealmHelper.findWordById(bgRealm, solvedWordId)
                solvedWord.state = WORD_STATE_SOLVED
                wordsAdapter!!.notifyItemChanged(solvedWord.position)
            }
            removeSelectedChunks()
            return true
        }
        return false
    }

    private fun isLevelSolved() {
        if (WordsRealmHelper.countNotSolvedWords(realm, level!!.id!!).toInt() == 0) {
            // Send the solved event to the Game activity
            callback!!.onLevelSolved()
        }
    }

    private fun removeSelectedChunks() {
        realm.executeTransaction { bgRealm ->
            val selectedChunks = ChunksRealmHelper.findSelectedChunksByLevelId(bgRealm, level!!.id!!)
            for (chunk in selectedChunks) {
                chunk.state = CHUNK_STATE_GONE.toLong()
                chunksAdapter!!.notifyItemChanged(chunk.position)
            }
        }
    }

    /**
     * Goes through the list of words to be guessed, and matches the input chunks with word chunks.
     *
     * @param words          the list of words to be solved.
     * @param selectedChunks the list of inputted chunks.
     * @return id of the solved word in the grid, and "" if none was solved.
     */
    private fun getSolvedWordId(words: RealmList<Word>?, selectedChunks: List<Chunk>): String? {
        for (word in words!!) {
            val wordId = word.id
            val equals = selectedChunks
                    .map { it.wordId }
                    .none { it != wordId }
            if (equals && word.word == selectedChunks.chunksToString()) {
                return word.id
            }
        }
        return ""
    }
}
