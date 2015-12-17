package im.ene.lab.attiq.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.data.event.Event;

/**
 * Created by eneim on 12/13/15.
 */
public class BaseActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
  }

  // placeholder for EventBus
  @SuppressWarnings("Unused")
  public void onEvent(Event event) {
  }

  @Override protected void onDestroy() {
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }
}
