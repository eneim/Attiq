package im.ene.lab.attiq.data.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.response.Article;

/**
 * Created by eneim on 12/13/15.
 */
public class ItemListEvent extends Event {

  private Article topArticle;

  private int page;

  public ItemListEvent(boolean isSuccess, @Nullable Error error, Article topArticle, int page) {
    super(isSuccess, error);
    this.topArticle = topArticle;
    this.page = page;
  }

  public Article getTopArticle() {
    return topArticle;
  }

  public void setTopArticle(Article topArticle) {
    this.topArticle = topArticle;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }
}
