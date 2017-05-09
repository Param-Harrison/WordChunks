package com.appchamp.wordchunks;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.appchamp.wordchunks.util.Constants.PREFS_REALM_CREATE_OBJECTS;
import static com.appchamp.wordchunks.util.Constants.WORD_CHUNKS_PREFERENCES;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLeakCanary();

        initStetho();

        initCalligraphy();

        RealmConfiguration config = initRealm();

        isRealmExists(config);

        initLogger();
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    private void initStetho() {
        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(
                            RealmInspectorModulesProvider.builder(this).build())
                    .build());
    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private RealmConfiguration initRealm() {
        Realm.init(this);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        return realmConfiguration;
    }

    private void isRealmExists(RealmConfiguration config) {
        SharedPreferences sp = getSharedPreferences(WORD_CHUNKS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        // Better this approach, because the user could clear cache data, and DB will be gone!
        // If Realm DB file exists.
        if (new File(config.getPath()).exists()) {
            // Putting in shared prefs true value
            editor.putBoolean(PREFS_REALM_CREATE_OBJECTS, false);
        } else {
            // doesn't exists if:
            // 1. user cleared data
            // 2. user just installed the app
            // put in shared prefs false value
            editor.putBoolean(PREFS_REALM_CREATE_OBJECTS, false);
        }
        editor.apply();
    }

    private void initLogger() {
        Logger.init().logLevel(LogLevel.FULL); // Use LogLevel.NONE for the release versions.
    }
}
