package im.ene.lab.attiq.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import im.ene.lab.attiq.activities.ItemDetailActivity;
import im.ene.lab.attiq.adapters.BaseAdapter;
import im.ene.lab.attiq.adapters.ListAdapter;
import im.ene.lab.attiq.adapters.UserItemsAdapter;
import im.ene.lab.attiq.data.one.PublicUser;
import im.ene.lab.attiq.data.zero.Post;
import im.ene.lab.attiq.widgets.DividerItemDecoration;

/**
 * Created by eneim on 1/6/16.
 */
public class UserItemsFragment extends ListFragment<Post> {

  private static final String ARGS_USER_ID = "attiq_fragment_args_user_id";

  private String mUserId;

  public UserItemsFragment() {

  }

  public static UserItemsFragment newInstance(String userId) {
    UserItemsFragment fragment = new UserItemsFragment();
    Bundle args = new Bundle();
    args.putString(ARGS_USER_ID, userId);
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override protected ListAdapter<Post> createAdapter() {
    return new UserItemsAdapter(mUserId);
  }

  private BaseAdapter.OnItemClickListener mItemClickListener;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
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

    mItemClickListener = new UserItemsAdapter.OnUserItemClickListener() {
      @Override public void onUserClick(PublicUser user) {
        // startActivity(ProfileActivity.createIntent(getContext(), user.getUrlName()));
      }

      @Override public void onItemContentClick(Post item) {
        startActivity(ItemDetailActivity.createIntent(getContext(), item.getUuid()));
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
