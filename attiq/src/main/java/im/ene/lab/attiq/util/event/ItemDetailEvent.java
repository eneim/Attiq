package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.two.Article;

/**
 * Created by eneim on 12/15/15.
 */
public class ItemDetailEvent extends Event {

  public final Article article;

  public ItemDetailEvent(boolean isSuccess, @Nullable Error error, Article article) {
    super(isSuccess, error);
    this.article = article;
  }
}
