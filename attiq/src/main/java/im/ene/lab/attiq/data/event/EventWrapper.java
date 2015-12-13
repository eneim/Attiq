package im.ene.lab.attiq.data.event;

/**
 * Created by eneim on 12/13/15.
 */
public class EventWrapper<T> extends Event {

  private T mLastItem;

  private int mPage;

  public EventWrapper(T lastItem, int page) {
    this.mLastItem = lastItem;
    this.mPage = page;
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
