package im.ene.lab.attiq.data.event;

import im.ene.lab.attiq.data.response.Item;

/**
 * Created by eneim on 12/13/15.
 */
public class ItemListEvent extends Event {

  private Item mTopItem;

  private int mPage;

  public Item getmTopItem() {
    return mTopItem;
  }

  public void setmTopItem(Item mTopItem) {
    this.mTopItem = mTopItem;
  }

  public int getmPage() {
    return mPage;
  }

  public void setmPage(int mPage) {
    this.mPage = mPage;
  }
}
