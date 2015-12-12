package im.ene.lab.attiq;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import io.fabric.sdk.android.Fabric;

/**
 * Created by eneim on 12/13/15.
 */
public class Attiq extends Application {

  @Override public void onCreate() {
    super.onCreate();
    Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
  }
}
