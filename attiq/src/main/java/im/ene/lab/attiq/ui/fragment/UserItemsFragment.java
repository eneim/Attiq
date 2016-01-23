package im.ene.lab.attiq.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import im.ene.lab.attiq.ui.activities.ItemDetailActivity;
import im.ene.lab.attiq.ui.adapters.ArticleListAdapter;
import im.ene.lab.attiq.ui.adapters.ListAdapter;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.UserArticlesAdapter;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.two.User;
import im.ene.lab.attiq.util.AnalyticsTrackers;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;

/**
 * Created by eneim on 1/6/16.
 */
public class UserItemsFragment extends ListFragment<Article> {

  private static final String ARGS_USER_ID = "attiq_fragment_args_user_id";

  private static final String SCREEN_NAME = "attiq:user:items";

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

  @Override protected void onVisibilityChange(boolean isVisibleToUser) {
    super.onVisibilityChange(isVisibleToUser);
    if (isVisibleToUser) {
      AnalyticsTrackers.sendScreenView(SCREEN_NAME);
    }
  }

  @NonNull @Override protected ListAdapter<Article> createAdapter() {
    return new UserArticlesAdapter(mUserId);
  }

  private OnItemClickListener mItemClickListener;

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

    mItemClickListener = new ArticleListAdapter.OnArticleClickListener() {
      @Override public void onUserClick(User user) {

      }

      @Override public void onItemContentClick(Article item) {
        startActivity(ItemDetailActivity.createIntent(getContext(), item.getId()));
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
