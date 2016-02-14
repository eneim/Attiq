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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import im.ene.lab.attiq.data.model.local.ReadArticle;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.two.User;
import im.ene.lab.attiq.ui.activities.ItemDetailActivity;
import im.ene.lab.attiq.ui.activities.ProfileActivity;
import im.ene.lab.attiq.ui.adapters.HistoryAdapter;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.AttiqRealmListAdapter;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;
import im.ene.lab.attiq.util.PrefUtil;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eneim on 1/23/16.
 */
public class HistoryFragment extends RealmListFragment<ReadArticle> {

  public static HistoryFragment newInstance() {
    return new HistoryFragment();
  }

  private Callback mCallback;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof Callback) {
      mCallback = (Callback) context;
    }
  }

  @NonNull @Override protected AttiqRealmListAdapter<ReadArticle> createRealmAdapter() {
    RealmResults<ReadArticle> articles =
        mRealm.where(ReadArticle.class).findAllSorted(ReadArticle.FIELD_LAST_VIEW, Sort.DESCENDING);
    return new HistoryAdapter(articles);
  }

  private OnItemClickListener mOnArticleClickListener;

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

    mOnArticleClickListener = new HistoryAdapter.OnArticleClickListener() {
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

    mAdapter.setOnItemClickListener(mOnArticleClickListener);

    if (mCallback != null) {
      mCallback.onHistoryShown(view);
    }
  }

  @Override public void onDetach() {
    mOnArticleClickListener = null;
    mCallback = null;
    super.onDetach();
  }

  public interface Callback {

    void onHistoryShown(View root);
  }
}
