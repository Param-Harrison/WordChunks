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
import com.appchamp.wordchunks.realmdb.utils.RealmFactory
import com.appchamp.wordchunks.util.Constants.LANG_RU
import com.appchamp.wordchunks.util.Constants.SUPPORTED_LOCALES
import com.facebook.stetho.Stetho
import com.franmontiel.localechanger.LocaleChanger
import com.squareup.leakcanary.LeakCanary
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm
import java.util.*

/**
 * DebugApp initializes:
 *
 * LocaleChanger
 * LeakCanary
 * Stetho
 * Realm
 *
 */
class DebugApp : Application() {

    override fun onCreate() {
        super.onCreate()

        LocaleChanger.initialize(applicationContext, SUPPORTED_LOCALES)

        initLeakCanary()

        initStetho()

        when {
        // User's system language is Russian
            Locale.getDefault().language.contentEquals(LANG_RU) -> initRealm(SUPPORTED_LOCALES[1].displayLanguage)
            else -> initRealm(SUPPORTED_LOCALES[0].displayLanguage)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleChanger.onConfigurationChanged()
    }

    /**
     * Initialize with default users configurations.
     */
    private fun initRealm(dbName: String) {
        Realm.init(this)
        val realmFactory: RealmFactory = RealmFactory()
        realmFactory.setRealmConfiguration(dbName)
        // For debugging only
        //Realm.deleteRealm(realmFactory.setRealmConfiguration(dbName))
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
