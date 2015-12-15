package im.ene.lab.attiq.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by eneim on 12/15/15.
 */
public class DataLoaderService extends IntentService {

  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */
  public DataLoaderService() {
    super(DataLoaderService.class.getSimpleName());
  }

  @Override protected void onHandleIntent(Intent intent) {

  }
}
