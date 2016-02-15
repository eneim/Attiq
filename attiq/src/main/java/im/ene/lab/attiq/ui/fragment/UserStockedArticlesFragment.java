/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.attiq.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import im.ene.lab.attiq.data.model.local.StockArticle;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.two.User;
import im.ene.lab.attiq.ui.activities.ItemDetailActivity;
import im.ene.lab.attiq.ui.activities.ProfileActivity;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.AttiqRealmListAdapter;
import im.ene.lab.attiq.ui.adapters.UserStockArticlesAdapter;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;
import im.ene.lab.attiq.util.PrefUtil;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eneim on 1/23/16.
 */
public class UserStockedArticlesFragment extends RealmListFragment<StockArticle> {

  private static final String ARGS_USER_ID = "attiq_fragment_args_user_id";

  public UserStockedArticlesFragment() {

  }

  public static UserStockedArticlesFragment newInstance(String userId) {
    UserStockedArticlesFragment fragment = new UserStockedArticlesFragment();
    Bundle args = new Bundle();
    args.putString(ARGS_USER_ID, userId);
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override protected AttiqRealmListAdapter<StockArticle> createRealmAdapter() {
    RealmResults<StockArticle> articles = mRealm.where(StockArticle.class)
        .equalTo(StockArticle.FIELD_USER_ID, mUserId)
        .findAllSorted(StockArticle.FIELD_CREATED_AT, Sort.DESCENDING);
    return new UserStockArticlesAdapter(mUserId, articles);
  }

  private String mUserId;

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

    OnItemClickListener onArticleClickListener =
        new UserStockArticlesAdapter.OnArticleClickListener() {
          @Override public void onUserClick(User user) {
            if (PrefUtil.checkNetwork(getContext())) {
              startActivity(ProfileActivity.createIntent(getContext(), user.getId()));
            }
          }

          @Override public void onItemContentClick(Article item) {
            if (PrefUtil.checkNetwork(getContext())) {
              startActivity(ItemDetailActivity.createIntent(getContext(), item.getId()));
            }
          }
        };

    mAdapter.setOnItemClickListener(onArticleClickListener);
  }

}
