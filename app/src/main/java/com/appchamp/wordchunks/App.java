package com.appchamp.wordchunks;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLeakCanary();

        initStetho();

        initCalligraphy();

        // 1. user cleared data
        // 2. user just installed the app
        RealmConfiguration config = initRealm();
        if (new File(config.getPath()).exists()) {
            // exists

        } else {
            // don't exists

        }

        // Initialize the entire Realm DB for the FIRST time,
        // when user just downloaded my app for the first time.
        // I must NOT change PREFS_FIRST into true ever again to persist my db.


        // If I want to update some fields in my realm db.
        // Load from file "data.json" when needed to update.
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
}
