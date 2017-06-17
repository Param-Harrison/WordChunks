package com.appchamp.wordchunks

import android.app.Application
import android.content.Context
import com.appchamp.wordchunks.util.Constants.PREFS_IS_DB_EXISTS
import com.appchamp.wordchunks.util.Constants.WORD_CHUNKS_PREFS
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.File


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initLeakCanary()

        initStetho()

        // The Realm lifecycle can be managed in the ViewModel and closed when the ViewModel is
        // no longer being used.
        // LiveData class works well with Realm’s observable live data, providing a layer
        // of abstraction so that the Activity isn’t exposed to RealmResults and RealmObjects.

        val config = initRealm()

        isRealmExists(config)
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

    private fun initRealm(): RealmConfiguration {
        Realm.init(this)

        val realmConfiguration = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        return realmConfiguration
    }

    private fun isRealmExists(config: RealmConfiguration) {
        val sp = getSharedPreferences(WORD_CHUNKS_PREFS, Context.MODE_PRIVATE)
        val editor = sp.edit()
        // Better this approach, because the user could clear cache data, and DB will be gone!
        // If Realm DB file exists.
        if (File(config.path).exists()) {
            // Putting in shared prefs true value
            editor.putBoolean(PREFS_IS_DB_EXISTS, false)  // false for debugging
        } else {
            // doesn't exists if:
            // 1. user cleared data
            // 2. user just installed the app
            // put in shared prefs false value
            editor.putBoolean(PREFS_IS_DB_EXISTS, false)
        }
        editor.apply()
        // Delete realm db before creating new objects.
        Realm.deleteRealm(RealmConfiguration.Builder().build())
    }
}
