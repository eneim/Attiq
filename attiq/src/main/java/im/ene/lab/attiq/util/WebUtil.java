package im.ene.lab.attiq.util;

import im.ene.lab.attiq.Attiq;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by eneim on 12/18/15.
 */
public class WebUtil {

  private static final String[] HEADERS = {
      "h0", "h1", "h2", "h3", "h4", "h5", "h6"
  };

  public static int getHeaderLevel(String header) {
    int level = -1;
    while (level++ < HEADERS.length) {
      if (HEADERS[level].equals(header)) {
        return level + 1;
      }
    }

    return level;
  }

  private static final OkHttpClient HTTP_CLIENT;

  static {
    HTTP_CLIENT = Attiq.httpClient();
  }

  private static final String TAG = "WebUtil";

  public static Call loadWeb(final String webUrl) {
    return HTTP_CLIENT.newCall(new Request.Builder().url(HttpUrl.parse(webUrl)).get().build());
  }
}
