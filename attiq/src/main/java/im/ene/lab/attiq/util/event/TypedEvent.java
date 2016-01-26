package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

/**
 * Created by eneim on 12/13/15.
 */
@Deprecated
public class TypedEvent<T> extends Event {

  public final T lastItem;

  public final int page;

  public TypedEvent(boolean isSuccess, @Nullable Error error, T lastItem, int page) {
    this(TypedEvent.class.getSimpleName(), isSuccess, error, lastItem, page);
  }

  public TypedEvent(@Nullable String tag, boolean success, @Nullable Error error, T lastItem, int
      page) {
    super(tag, success, error);
    this.lastItem = lastItem;
    this.page = page;
  }
}
