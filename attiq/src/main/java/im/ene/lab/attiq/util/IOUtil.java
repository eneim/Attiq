package im.ene.lab.attiq.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import okio.BufferedSource;
import okio.Okio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

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

  public static String readRaw(@NonNull Context context, int rawFile) throws IOException {
    InputStream stream = context.getResources().openRawResource(rawFile);
    BufferedSource buffer = Okio.buffer(Okio.source(stream));
    return buffer.readString(Charset.forName("utf-8"));
  }

  public static String readAssets(@NonNull Context context, String fileName) throws IOException {
    AssetManager assetManager = context.getResources().getAssets();
    InputStream stream = assetManager.open(fileName);
    BufferedSource buffer = Okio.buffer(Okio.source(stream));
    return buffer.readString(Charset.forName("utf-8"));
  }

}
