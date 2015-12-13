package im.ene.lab.attiq.fragment;

import android.support.annotation.NonNull;

import im.ene.lab.attiq.adapters.BaseListAdapter;
import im.ene.lab.attiq.adapters.ItemListAdapter;
import im.ene.lab.attiq.data.event.EventWrapper;
import im.ene.lab.attiq.data.response.Item;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eneim on 12/13/15.
 */
public class ItemListFragment extends RealmListFragment<Item> {

  public ItemListFragment() {

  }

  public static ItemListFragment newInstance() {
    return new ItemListFragment();
  }

  @NonNull @Override protected BaseListAdapter<Item> createAdapter() {
    RealmResults<Item> items = mRealm.where(Item.class)
        .findAllSorted("updatedAt", Sort.DESCENDING);
    return new ItemListAdapter(items);
  }

  @Override public void onEventMainThread(EventWrapper<Item> event) {

  }
}
