package im.ene.lab.attiq;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;

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

  public static Attiq attiqCreator() {
    return INSTANCE;
  }

  public static Realm realm() {
    return Realm.getDefaultInstance();
  }

  @Override public void onCreate() {
    super.onCreate();
    INSTANCE = this;
    Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

    RealmConfiguration config = new RealmConfiguration.Builder(this)
        .name(getString(R.string.realm_name))
        .schemaVersion(R.integer.realm_version)
        .migration(new RealmMigration() {
          @Override public void migrate(DynamicRealm dynamicRealm, long oldVer, long newVer) {

          }
        })
        .build();

    Realm.setDefaultConfiguration(config);

  }
}
