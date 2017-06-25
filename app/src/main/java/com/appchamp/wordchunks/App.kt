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

package com.appchamp.wordchunks

import android.app.Application
import android.content.res.Configuration
import com.appchamp.wordchunks.realmdb.models.pojo.packsFromJSONFile
import com.appchamp.wordchunks.realmdb.utils.gameModel
import com.appchamp.wordchunks.util.Constants.FILE_NAME_DATA
import com.appchamp.wordchunks.util.Constants.FILE_NAME_DATA_RU
import com.appchamp.wordchunks.util.Constants.LANG_RU
import com.appchamp.wordchunks.util.Constants.SUPPORTED_LOCALES
import com.facebook.stetho.Stetho
import com.franmontiel.localechanger.LocaleChanger
import com.squareup.leakcanary.LeakCanary
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.File
import java.util.*


// The Realm lifecycle can be managed in the ViewModel and closed when the ViewModel is
// no longer being used.
// LiveData class works well with Realm’s observable live data, providing a layer
// of abstraction so that the Activity isn’t exposed to RealmResults and RealmObjects.

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        LocaleChanger.initialize(applicationContext, SUPPORTED_LOCALES)

        initLeakCanary()

        initStetho()

        // User's system language is Russian
        if (Locale.getDefault().language.contentEquals(LANG_RU)) {
            initRealm(FILE_NAME_DATA_RU)
        } else { // User's system language is English or else
            initRealm(FILE_NAME_DATA)
        }
    }

    /**
     * Initialize with default users configurations.
     */
    private fun initRealm(name: String) {
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
                .name(name)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)

        // If realm database doesn't exists then delete before creating new objects.
        if (!File(realmConfiguration.path).exists()) {
            Realm.deleteRealm(realmConfiguration)

            val packs = packsFromJSONFile(baseContext, name + ".json")
            if (packs.isEmpty()) return

            Realm.getDefaultInstance().use {
                it.gameModel().createPacks(packs)
                it.gameModel().initFirstGameSatate()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleChanger.onConfigurationChanged()
    }

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }

    private fun initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                RealmInspectorModulesProvider.builder(this).build())
                        .build())
    }
}
