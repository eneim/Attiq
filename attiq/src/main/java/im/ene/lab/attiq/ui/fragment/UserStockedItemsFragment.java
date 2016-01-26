package im.ene.lab.attiq.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.ui.activities.ItemDetailActivity;
import im.ene.lab.attiq.ui.activities.ProfileActivity;
import im.ene.lab.attiq.ui.adapters.ArticleListAdapter;
import im.ene.lab.attiq.ui.adapters.ListAdapter;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.UserStockArticlesAdapter;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.two.User;
import im.ene.lab.attiq.util.AnalyticsUtil;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;
import io.realm.Realm;
import retrofit2.Response;

import java.util.List;

/**
 * Created by eneim on 1/6/16.
 */
public class UserStockedItemsFragment extends ListFragment<Article> {

  private static final String ARGS_USER_ID = "attiq_fragment_args_user_id";

  private static final String SCREEN_NAME = "attiq:user:stock_items";

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

  @Override protected void onVisibilityChange(boolean isVisibleToUser) {
    super.onVisibilityChange(isVisibleToUser);
    if (isVisibleToUser) {
      AnalyticsUtil.sendScreenView(SCREEN_NAME);
    }
  }

  @NonNull @Override protected ListAdapter<Article> createAdapter() {
    return new UserStockArticlesAdapter(mUserId);
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
        startActivity(ProfileActivity.createIntent(getContext(), user.getId()));
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

  @Override public void onResponse(Response<List<Article>> response) {
    super.onResponse(response);
    List<Article> posts = response.body();
    Realm realm = Attiq.realm();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(posts);
    realm.commitTransaction();
    realm.close();
  }
}
