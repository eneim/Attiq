package im.ene.lab.attiq.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import im.ene.lab.attiq.data.model.one.PublicUser;
import im.ene.lab.attiq.data.model.zero.PublicPost;
import im.ene.lab.attiq.ui.activities.ItemDetailActivity;
import im.ene.lab.attiq.ui.activities.ProfileActivity;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.PublicItemsAdapter;
import im.ene.lab.attiq.ui.adapters.RealmListAdapter;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;
import im.ene.lab.attiq.util.PrefUtil;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eneim on 12/14/15.
 */
public class PublicStreamFragment extends RealmListFragment<PublicPost> {

  private static final String SCREEN_NAME = "attiq:home:public_items";

  public PublicStreamFragment() {

  }

  public static PublicStreamFragment newInstance() {
    return new PublicStreamFragment();
  }

  @NonNull @Override protected RealmListAdapter<PublicPost> createRealmAdapter() {
    RealmResults<PublicPost> items =
        mRealm.where(PublicPost.class).findAllSorted("createdAtAsSeconds", Sort.DESCENDING);
    return new PublicItemsAdapter(items);
  }

  private OnItemClickListener mItemClickListener;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

    mItemClickListener = new PublicItemsAdapter.OnPublicItemClickListener() {
      @Override public void onUserClick(PublicUser user) {
        if (PrefUtil.checkNetwork(getContext())) {
          startActivity(ProfileActivity.createIntent(getContext(), user.getUrlName()));
        }
      }

      @Override public void onItemContentClick(PublicPost item) {
        if (PrefUtil.checkNetwork(getContext())) {
          startActivity(ItemDetailActivity.createIntent(getContext(), item.getUuid()));
        }
      }
    };

    mAdapter.setOnItemClickListener(mItemClickListener);
  }

  private static final String TAG = "PublicStreamFragment";

  @Override public void onDestroyView() {
    // no UI interaction after this point;
    mItemClickListener = null;
    super.onDestroyView();
  }

}
