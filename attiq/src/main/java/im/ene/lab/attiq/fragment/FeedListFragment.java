package im.ene.lab.attiq.fragment;

import android.support.annotation.NonNull;

import im.ene.lab.attiq.adapters.AttiqListAdapter;
import im.ene.lab.attiq.data.api.open.FeedItem;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedListFragment extends RealmListFragment<FeedItem> {

  @NonNull @Override protected AttiqListAdapter<FeedItem> createAdapter() {
    return null;
  }
}
