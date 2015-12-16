package im.ene.lab.attiq.data.event;

import android.support.annotation.Nullable;

/**
 * Created by eneim on 12/13/15.
 */
public class EventWrapper<T> extends Event {

  private T lastItem;

  private int page;

  public EventWrapper(boolean isSuccess, @Nullable Error error, T lastItem, int page) {
    super(isSuccess, error);
    this.lastItem = lastItem;
    this.page = page;
  }

  public T getLastItem() {
    return lastItem;
  }

  public void setLastItem(T lastItem) {
    this.lastItem = lastItem;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }
}
