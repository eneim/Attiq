package im.ene.lab.attiq.util;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by eneim on 1/16/16.
 */
public class AsyncTaskUtil {

  public static abstract class Callback<T> {

    protected void onStart() {
    }

    protected abstract void onFinished(T object);
  }

  public static void load(final String folder, final Callback<String> callback) {
    new AsyncTask<Void, Void, String>() {

      @Override protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null) {
          callback.onStart();
        }
      }

      @Override protected String doInBackground(Void... params) {
        try {
          return IOUtil.readAssetFolder(folder);
        } catch (IOException e) {
          e.printStackTrace();
          return null;
        }
      }

      @Override protected void onPostExecute(String s) {
        if (callback != null) {
          callback.onFinished(s);
        }
      }
    }.execute();
  }

}
