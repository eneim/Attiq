package im.ene.lab.attiq.activities;

import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.util.event.Event;

/**
 * Created by eneim on 12/13/15.
 */
public class BaseActivity extends AppCompatActivity {

  // placeholder for EventBus
  @SuppressWarnings("unused")
  public void onEvent(Event event) {
  }

  @Override protected void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override protected void onPause() {
    EventBus.getDefault().unregister(this);
    super.onPause();
  }
}
