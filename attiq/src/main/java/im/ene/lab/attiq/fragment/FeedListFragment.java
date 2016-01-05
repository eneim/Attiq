package im.ene.lab.attiq.fragment;

import android.support.annotation.NonNull;

import im.ene.lab.attiq.adapters.AttiqListAdapter;
import im.ene.lab.attiq.adapters.FeedAdapter;
import im.ene.lab.attiq.data.api.open.FeedItem;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedListFragment extends RealmListFragment<FeedItem> {

  public FeedListFragment() {

  }

  public static FeedListFragment newInstance() {
    return new FeedListFragment();
  }

  @NonNull @Override protected AttiqListAdapter<FeedItem> createAdapter() {
    RealmResults<FeedItem> items = mRealm.where(FeedItem.class)
        .findAllSorted("createdAtInUnixtime", Sort.DESCENDING);
    return new FeedAdapter(items);
  }

  @Override public void onFailure(Throwable t) {
    super.onFailure(t);
  }
}
