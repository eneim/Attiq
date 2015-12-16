package im.ene.lab.attiq.data.event;

import android.support.annotation.Nullable;

/**
 * Created by eneim on 12/13/15.
 */
public class EventWrapper<T> extends Event {

  private T mLastItem;

  private int mPage;

  public EventWrapper(boolean isSuccess, @Nullable Error error, T mLastItem, int mPage) {
    super(isSuccess, error);
    this.mLastItem = mLastItem;
    this.mPage = mPage;
  }

  public T getmLastItem() {
    return mLastItem;
  }

  public void setmLastItem(T mLastItem) {
    this.mLastItem = mLastItem;
  }

  public int getmPage() {
    return mPage;
  }

  public void setmPage(int mPage) {
    this.mPage = mPage;
  }
}
