package im.ene.lab.attiq.data.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.api.v2.response.Article;

/**
 * Created by eneim on 12/13/15.
 */
public class ItemListEvent extends Event {

  public final Article topArticle;

  public final int page;

  public ItemListEvent(boolean isSuccess, @Nullable Error error, Article topArticle, int page) {
    super(isSuccess, error);
    this.topArticle = topArticle;
    this.page = page;
  }
}
