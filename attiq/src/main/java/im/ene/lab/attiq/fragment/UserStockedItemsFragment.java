package im.ene.lab.attiq.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import im.ene.lab.attiq.activities.ItemDetailActivity;
import im.ene.lab.attiq.activities.ProfileActivity;
import im.ene.lab.attiq.adapters.BaseAdapter;
import im.ene.lab.attiq.adapters.RealmListAdapter;
import im.ene.lab.attiq.adapters.UserStockItemsAdapter;
import im.ene.lab.attiq.data.one.PublicUser;
import im.ene.lab.attiq.data.one.UserStockItem;
import im.ene.lab.attiq.widgets.DividerItemDecoration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eneim on 1/6/16.
 */
public class UserStockedItemsFragment extends RealmListFragment<UserStockItem> {

  private static final String ARGS_USER_ID = "attiq_fragment_args_user_id";

  private String mUserId;

  public UserStockedItemsFragment() {

  }

  public static UserStockedItemsFragment newInstance(String userId) {
    UserStockedItemsFragment fragment = new UserStockedItemsFragment();
    Bundle args = new Bundle();
    args.putString(ARGS_USER_ID, userId);
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override protected RealmListAdapter<UserStockItem> createAdapter() {
    RealmResults<UserStockItem> items = mRealm.where(UserStockItem.class)
        .findAllSorted("createdAtAsSeconds", Sort.DESCENDING);
    return new UserStockItemsAdapter(mUserId, items);
  }

  private BaseAdapter.OnItemClickListener mItemClickListener;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    mRealm.beginTransaction();
    mRealm.clear(UserStockItem.class);
    mRealm.commitTransaction();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mUserId = getArguments().getString(ARGS_USER_ID);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
        DividerItemDecoration.VERTICAL_LIST));

    mItemClickListener = new UserStockItemsAdapter.OnUserItemClickListener() {
      @Override public void onUserClick(PublicUser user) {
        startActivity(ProfileActivity.createIntent(getContext(), user.getUrlName()));
      }

      @Override public void onItemContentClick(UserStockItem item) {
        startActivity(ItemDetailActivity.createIntent(getContext(), item.getId(), item.getUuid()));
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
