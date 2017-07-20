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

package com.appchamp.wordchunks.ui.hint

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.models.realm.Word
import com.appchamp.wordchunks.realmdb.utils.LiveRealmObject
import com.appchamp.wordchunks.realmdb.utils.chunkModel
import com.appchamp.wordchunks.realmdb.utils.levelModel
import com.appchamp.wordchunks.realmdb.utils.wordModel
import io.realm.Realm


class HintViewModel(application: Application, levelId: String) : AndroidViewModel(application) {

    private val db: Realm = Realm.getDefaultInstance()
    // Live data
    private var level: LiveRealmObject<Level>
    private lateinit var selectedWord: LiveRealmObject<Word>

    init {
        // Load level from the Realm db as LiveData
        level = db.levelModel().findLevelById(levelId)
    }

    fun getLevel(): LiveRealmObject<Level> = level

    fun getSelectedWord(): LiveRealmObject<Word> {
        return selectedWord
    }

    fun setSelectedWord(wordId: String) {
        selectedWord = db.wordModel().findWordById(wordId)
    }

    fun getWordPosition(): String {
        return ((selectedWord.value?.position)?.plus(1)).toString()
    }

    fun getWordLength(): Int {
        return selectedWord.value?.word?.length!!
    }

    fun getFirstLetter(): String {
        return selectedWord.value?.word?.take(1)!!
    }

    fun getFirstChunk(): String {
        val firstChunk = db.chunkModel().findChunkByWordId(selectedWord.value?.id!!)
        return firstChunk.chunk
    }

    fun getWord(): String {
        return selectedWord.value?.word!!
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel... Like RealmResults and the instance of Realm!
     */
    override fun onCleared() {
        db.close()
        super.onCleared()
    }

    /**
     * A creator is used to inject the level ID into the ViewModel.
     */
    class Factory(private val application: Application, private val levelId: String)
        : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HintViewModel(application, levelId) as T
        }
    }

}