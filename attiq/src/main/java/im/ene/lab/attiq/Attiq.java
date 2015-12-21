package im.ene.lab.attiq;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import im.ene.lab.attiq.util.TimeUtil;
import io.fabric.sdk.android.Fabric;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by eneim on 12/13/15.
 */
public class Attiq extends Application {

  private static Attiq INSTANCE;
  private SharedPreferences mPreference;
  private Picasso mPicasso;

  public static Realm realm() {
    return Realm.getDefaultInstance();
  }

  public static SharedPreferences pref() {
    return creator().mPreference;
  }

  public static Picasso picasso() {
    return creator().mPicasso;
  }

  public static Attiq creator() {
    return INSTANCE;
  }

  @Override public void onCreate() {
    super.onCreate();
    INSTANCE = this;
    Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

    TimeUtil.init(this);

    RealmConfiguration config = new RealmConfiguration.Builder(this)
        .name(getString(R.string.realm_name))
        .schemaVersion(R.integer.realm_version)
        .migration(new RealmMigration() {
          @Override public void migrate(DynamicRealm dynamicRealm, long oldVer, long newVer) {

          }
        }).build();

    Realm.setDefaultConfiguration(config);

    mPreference = getSharedPreferences(getPackageName() + "_pref", Context.MODE_PRIVATE);
    mPicasso = new Picasso.Builder(this)
        .defaultBitmapConfig(Bitmap.Config.RGB_565)
        .build();

    Stetho.initialize(
        Stetho.newInitializerBuilder(this)
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
            .build());

  }
}
