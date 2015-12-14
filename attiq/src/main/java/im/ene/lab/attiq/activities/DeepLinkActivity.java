package im.ene.lab.attiq.activities;

import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by eneim on 12/15/15.
 */
public class DeepLinkActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TextView view = new TextView(this);
    view.setText("TEST");
    setContentView(view);
  }
}
