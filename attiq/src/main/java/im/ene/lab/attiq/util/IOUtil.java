package im.ene.lab.attiq.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.res.AssetManager;

import im.ene.lab.attiq.Attiq;
import io.realm.RealmObject;
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
    GSON = new GsonBuilder()
        .setExclusionStrategies(new ExclusionStrategy() {
          @Override
          public boolean shouldSkipField(FieldAttributes f) {
            return f.getDeclaringClass().equals(RealmObject.class);
          }

          @Override
          public boolean shouldSkipClass(Class<?> clazz) {
            return false;
          }
        })
        .excludeFieldsWithoutExposeAnnotation().create();
  }

  public static Gson gson() {
    return GSON;
  }

  public static String readRaw(int rawFileId) throws IOException {
    InputStream stream = Attiq.creator().getResources().openRawResource(rawFileId);
    BufferedSource buffer = Okio.buffer(Okio.source(stream));
    return buffer.readString(Charset.forName("utf-8"));
  }

  public static String readAssets(String fileName) throws IOException {
    AssetManager assetManager = Attiq.creator().getResources().getAssets();
    InputStream stream = assetManager.open(fileName);
    BufferedSource buffer = Okio.buffer(Okio.source(stream));
    return buffer.readString(Charset.forName("utf-8"));
  }

}
