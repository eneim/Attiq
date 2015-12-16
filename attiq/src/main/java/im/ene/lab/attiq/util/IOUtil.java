package im.ene.lab.attiq.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by eneim on 12/13/15.
 */
public class IOUtil {

  private static final Gson GSON;

  static {
    GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
  }

  public static Gson gson() {
    return GSON;
  }

  public static String readAllFromAssets(Context context, String target) throws IOException {
    AssetManager as = context.getApplicationContext().getResources().getAssets();

    StringBuilder sb = new StringBuilder();

    InputStream is = null;
    BufferedReader br = null;
    try {
      is = as.open(target);
      br = new BufferedReader(new InputStreamReader(is));

      String s;
      while ((s = br.readLine()) != null) {
        sb.append(s).append("\n");
      }
    } finally {
      closeQuietly(is);
      closeQuietly(br);
    }

    return sb.toString();
  }

  public static void closeQuietly(Closeable closeable) {
    if (closeable == null) {
      return;
    }
    try {
      closeable.close();
    } catch (IOException ignore) {
      //Do nothing
    }
  }
}
