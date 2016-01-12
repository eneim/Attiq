package im.ene.lab.attiq.data;

import im.ene.lab.attiq.data.two.Article;

import java.util.List;

/**
 * Created by eneim on 1/12/16.
 */
public abstract class SearchDataManager {

  public abstract void onDataLoaded(List<Article> data);

  private String query = "eneim";

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public void clear() {

  }

  public void searchFor(String query) {

  }
}
