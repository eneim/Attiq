package im.ene.lab.attiq.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by eneim on 12/13/15.
 */
public class JsonUtil {

  private static final Gson GSON;

  static {
    GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
  }

  public static Gson gson() {
    return GSON;
  }
}
