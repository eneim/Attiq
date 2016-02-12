package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.model.two.Article;

/**
 * Created by eneim on 12/15/15.
 */
public class ItemDetailEvent extends Event {

  public final Article article;

  public ItemDetailEvent(@Nullable String tag, boolean success, @Nullable Error error, Article
      article) {
    super(tag, success, error);
    this.article = article;
  }
}
