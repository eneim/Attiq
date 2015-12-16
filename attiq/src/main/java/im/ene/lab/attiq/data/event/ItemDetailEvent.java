package im.ene.lab.attiq.data.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.response.Item;

/**
 * Created by eneim on 12/15/15.
 */
public class ItemDetailEvent extends Event {

  private Item item;

  public ItemDetailEvent(boolean isSuccess, @Nullable Error error, Item item) {
    super(isSuccess, error);
    this.item = item;
  }

  public Item getItem() {
    return item;
  }
}
