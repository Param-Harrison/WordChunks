package com.appchamp.wordchunks

import android.app.Application
import android.content.Context
import com.appchamp.wordchunks.util.Constants.PREFS_REALM_CREATE_OBJECTS
import com.appchamp.wordchunks.util.Constants.WORD_CHUNKS_PREFS
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm
import io.realm.RealmConfiguration
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import java.io.File


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initLeakCanary()

        initStetho()

        initCalligraphy()

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

    private fun initCalligraphy() {
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/nunitoregular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
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
            editor.putBoolean(PREFS_REALM_CREATE_OBJECTS, false)
        } else {
            // doesn't exists if:
            // 1. user cleared data
            // 2. user just installed the app
            // put in shared prefs false value
            editor.putBoolean(PREFS_REALM_CREATE_OBJECTS, false)
        }
        editor.apply()
    }
}
