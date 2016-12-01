package im.ene.lab.attiq;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.squareup.picasso.Picasso;
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
    // TODO Init Analytics

    // Fabric, Answer, Crashlytics, ...
    Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

    // Batch.Push.setGCMSenderId(getString(R.string.gcm_defaultSenderId));
    // Batch.setConfig(new Config(getString(R.string.batch_com_api_key)));

    // Date, Time, ...
    TimeUtil.init(this);
    // Realm
    final RealmConfiguration config =
        new RealmConfiguration.Builder(this).name(getString(R.string.realm_name))
            .schemaVersion(getResources().getInteger(R.integer.realm_version_2_0_0))
            .deleteRealmIfMigrationNeeded()
            .build();

    Realm.setDefaultConfiguration(config);

    mHttpClient = new OkHttpClient();
    mPicasso = new Picasso.Builder(this)
        // .defaultBitmapConfig(Bitmap.Config.RGB_565)
        .downloader(new OkHttp3Downloader(mHttpClient))  // a separated client
        .build();
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    TimeUtil.init(this);  // On language change --> need update
  }
}
