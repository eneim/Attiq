package im.ene.lab.attiq.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import im.ene.lab.attiq.activities.ItemDetailActivity2;
import im.ene.lab.attiq.activities.ProfileActivity;
import im.ene.lab.attiq.adapters.BaseListAdapter;
import im.ene.lab.attiq.adapters.TimeLineAdapter;
import im.ene.lab.attiq.data.event.EventWrapper;
import im.ene.lab.attiq.data.vault.PublicItem;
import im.ene.lab.attiq.data.vault.PublicUser;
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
    return new TimeLineAdapter(items);
  }

  @Override public void onEventMainThread(EventWrapper<PublicItem> event) {

  }

  private TimeLineAdapter.OnTimeLineItemClickListener mItemClickListener;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
        DividerItemDecoration.VERTICAL_LIST));

    mItemClickListener = new TimeLineAdapter.OnTimeLineItemClickListener() {
      @Override public void onUserClick(PublicUser user) {
        startActivity(ProfileActivity.createIntent(getContext()));
      }

      @Override public void onItemContentClick(PublicItem item) {
        startActivity(ItemDetailActivity2.createIntent(getContext(), item));
      }
    };

    mAdapter.setOnItemClickListener(mItemClickListener);
  }

  @Override public void onDestroyView() {
    // no UI interaction after this point;
    mItemClickListener = null;
    super.onDestroyView();
  }
}
