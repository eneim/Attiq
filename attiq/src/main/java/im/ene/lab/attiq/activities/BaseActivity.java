package im.ene.lab.attiq.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.data.event.Event;

/**
 * Created by eneim on 12/13/15.
 */
public class BaseActivity extends AppCompatActivity {

  protected EventBus mEventBus;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mEventBus = new EventBus();
    mEventBus.register(this);
  }

  @SuppressWarnings("Unused")
  public void onEvent(Event event) {
  }

  @Override protected void onDestroy() {
    mEventBus.unregister(this);
    mEventBus = null;
    super.onDestroy();
  }
}
