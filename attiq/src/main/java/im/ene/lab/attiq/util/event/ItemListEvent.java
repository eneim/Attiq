package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.model.two.Article;

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
