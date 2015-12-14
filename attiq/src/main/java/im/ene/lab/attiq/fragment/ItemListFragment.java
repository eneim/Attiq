package im.ene.lab.attiq.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import im.ene.lab.attiq.adapters.BaseListAdapter;
import im.ene.lab.attiq.adapters.ItemListAdapter;
import im.ene.lab.attiq.data.event.EventWrapper;
import im.ene.lab.attiq.data.response.Item;
import im.ene.lab.attiq.widgets.DividerItemDecoration;
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

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
        DividerItemDecoration.VERTICAL_LIST));
  }
}
