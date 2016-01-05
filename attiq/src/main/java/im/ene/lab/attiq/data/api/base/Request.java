package im.ene.lab.attiq.data.api.base;

/**
 * Created by eneim on 12/25/15.
 */
public abstract class Request {

  public final boolean isLoadingMore;

  public Request(boolean isLoadingMore) {
    this.isLoadingMore = isLoadingMore;
  }
}
