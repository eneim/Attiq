package im.ene.lab.attiq.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by eneim on 1/14/16.
 */
public abstract class DocumentCallback implements okhttp3.Callback {

  private final String baseUrl;

  public DocumentCallback(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override public void onFailure(Request request, IOException e) {
    onDocument(null);
  }

  @Override public void onResponse(okhttp3.Response response) throws IOException {
    ResponseBody body = response.body();
    InputStream stream = body == null ? null : body.byteStream();
    if (stream != null) {
      Document document = Jsoup.parse(stream, "utf-8", baseUrl);
      onDocument(document);
    }
  }

  public abstract void onDocument(Document response);
}
