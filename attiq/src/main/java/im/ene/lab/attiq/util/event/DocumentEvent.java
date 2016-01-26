package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import org.jsoup.nodes.Document;

/**
 * Created by eneim on 1/7/16.
 * <p/>
 * Document which is parsed from {@link org.jsoup.Jsoup}
 */
public class DocumentEvent extends Event {

  public final Document document;

  public DocumentEvent(@Nullable String tag, boolean success, @Nullable Error error, Document
      document) {
    super(tag, success, error);
    this.document = document;
  }
}
