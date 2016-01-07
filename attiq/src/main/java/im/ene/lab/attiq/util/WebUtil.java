package im.ene.lab.attiq.util;

import org.jsoup.Jsoup;

import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.util.event.DocumentEvent;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

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

  public static void loadWeb(final String webUrl) {
    HTTP_CLIENT.newCall(new Request.Builder().url(HttpUrl.parse(webUrl)).get().build())
        .enqueue(new Callback() {
          @Override public void onFailure(Request request, IOException e) {

          }

          @Override public void onResponse(Response response) throws IOException {
            ResponseBody body = response.body();
            InputStream stream = body == null ? null : body.byteStream();
            if (stream != null) {
              EventBus.getDefault().post(
                  new DocumentEvent(true, null, Jsoup.parse(stream, "utf-8", webUrl)));
            }
          }
        });
  }
}
