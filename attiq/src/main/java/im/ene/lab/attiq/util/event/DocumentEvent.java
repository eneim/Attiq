package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import org.jsoup.nodes.Document;

/**
 * Created by eneim on 1/7/16.
 */
public class DocumentEvent extends Event {

  public final Document document;

  public DocumentEvent(boolean success, @Nullable Error error, Document document) {
    super(success, error);
    this.document = document;
  }
}
