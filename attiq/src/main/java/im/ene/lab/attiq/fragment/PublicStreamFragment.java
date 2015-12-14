package im.ene.lab.attiq.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import im.ene.lab.attiq.adapters.BaseListAdapter;
import im.ene.lab.attiq.adapters.PublicItemsAdapter;
import im.ene.lab.attiq.data.event.EventWrapper;
import im.ene.lab.attiq.data.vault.PublicItem;
import im.ene.lab.attiq.widgets.DividerItemDecoration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eneim on 12/14/15.
 */
public class PublicStreamFragment extends RealmListFragment<PublicItem> {

  public PublicStreamFragment() {

  }

  public static PublicStreamFragment newInstance() {
    return new PublicStreamFragment();
  }

  @NonNull @Override protected BaseListAdapter<PublicItem> createAdapter() {
    RealmResults<PublicItem> items = mRealm.where(PublicItem.class)
        .findAllSorted("createdAtAsSeconds", Sort.DESCENDING);
    return new PublicItemsAdapter(items);
  }

  @Override public void onEventMainThread(EventWrapper<PublicItem> event) {

  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
        DividerItemDecoration.VERTICAL_LIST));
  }
}
