package im.ene.lab.attiq.data.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.api.v2.response.Article;

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
