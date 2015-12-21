package im.ene.lab.attiq.data.event;

import android.support.annotation.Nullable;

/**
 * Created by eneim on 12/13/15.
 */
public class EventWrapper<T> extends Event {

  public final T lastItem;

  public final int page;

  public EventWrapper(boolean isSuccess, @Nullable Error error, T lastItem, int page) {
    super(isSuccess, error);
    this.lastItem = lastItem;
    this.page = page;
  }

}
