package im.ene.lab.attiq.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import im.ene.lab.attiq.Attiq;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

  public static class AttiqImageGetter implements Html.ImageGetter {

    private final Context context;
    private final View container;

    public AttiqImageGetter(Context context, View container) {
      this.context = context;
      this.container = container;
    }

    @Override public Drawable getDrawable(String source) {
      final URLDrawable urlDrawable = new URLDrawable();
      HTTP_CLIENT.newCall(new Request.Builder().url(HttpUrl.parse(source)).get().build())
          .enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

            }

            @Override public void onResponse(Call call, Response response) throws IOException {
              InputStream is = response.body().byteStream();
              Drawable drawable = Drawable.createFromStream(is, "src");
              drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

              // set the correct bound according to the result from HTTP call
              urlDrawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
                  .getIntrinsicHeight());

              urlDrawable.drawable = drawable;
              AttiqImageGetter.this.container.invalidate();
            }
          });

      return urlDrawable;
    }
  }

  private static class URLDrawable extends BitmapDrawable {
    // the drawable that you need to set, you could set the initial drawing
    // with the loading image if you need to
    protected Drawable drawable;

    @Override
    public void draw(Canvas canvas) {
      // override the draw to facilitate refresh function later
      if (drawable != null) {
        drawable.draw(canvas);
      }
    }
  }
}
