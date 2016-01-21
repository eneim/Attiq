package im.ene.lab.attiq;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.squareup.picasso.Picasso;

import im.ene.lab.attiq.util.AnalyticsTrackers;
import im.ene.lab.attiq.util.TimeUtil;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;

/**
 * Created by eneim on 12/13/15.
 */
public class Attiq extends Application {

  private static Attiq INSTANCE;
  private SharedPreferences mPreference;
  private Picasso mPicasso;
  private OkHttpClient mHttpClient;

  public static Realm realm() {
    return Realm.getDefaultInstance();
  }

  public static SharedPreferences pref() {
    return creator().mPreference;
  }

  public static Picasso picasso() {
    return creator().mPicasso;
  }

  public static OkHttpClient httpClient() {
    return creator().mHttpClient;
  }

  public static Attiq creator() {
    return INSTANCE;
  }

  @Override public void onCreate() {
    super.onCreate();
    INSTANCE = this;

    mPreference = getSharedPreferences(getPackageName() + "_pref", Context.MODE_PRIVATE);
    // Call only once
    AnalyticsTrackers.initialize(this);

    Parse.enableLocalDatastore(getApplicationContext());
    Parse.initialize(this);
    ParseInstallation.getCurrentInstallation().saveInBackground();
    ParseACL defaultACL = new ParseACL();
    ParseACL.setDefaultACL(defaultACL, true);
    // Fabric, Answer, Crashlytics, ...
    Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

    // Date, Time, ...
    TimeUtil.init(this);
    // Realm
    final RealmConfiguration config = new RealmConfiguration.Builder(this)
        .name(getString(R.string.realm_name))
        .schemaVersion(R.integer.realm_version)
        .deleteRealmIfMigrationNeeded()
        .build();

    Realm.setDefaultConfiguration(config);

    mHttpClient = new OkHttpClient();
    mPicasso = new Picasso.Builder(this)
        // .defaultBitmapConfig(Bitmap.Config.RGB_565)
        .downloader(new OkHttp3Downloader(mHttpClient))  // a separated client
        .build();

//    Stetho.initialize(
//        Stetho.newInitializerBuilder(this)
//            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
//            .build());

  }
}
