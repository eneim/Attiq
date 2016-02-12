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

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.zero.FeedItem;
import im.ene.lab.attiq.ui.activities.ItemDetailActivity;
import im.ene.lab.attiq.ui.activities.ProfileActivity;
import im.ene.lab.attiq.ui.adapters.FeedListAdapter;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.RealmListAdapter;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ItemDetailEvent;
import im.ene.lab.attiq.util.event.ItemsEvent;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedListFragment extends RealmListFragment<FeedItem> {

  private static final String SCREEN_NAME = "attiq:home:feed_list";

  public FeedListFragment() {

  }

  public static FeedListFragment newInstance() {
    return new FeedListFragment();
  }

  private static final String TAG = "FeedListFragment";

  private OnItemClickListener mOnItemClickListener;

  private Callback<Article> mOnArticleLoaded = new Callback<Article>() {
    @Override public void onResponse(Call<Article> call, Response<Article> response) {
      Article article = response.body();
      if (article != null) {
        EventBus.getDefault()
            .post(new ItemDetailEvent(getClass().getSimpleName(), true, null, article));
      } else {
        EventBus.getDefault()
            .post(new ItemDetailEvent(getClass().getSimpleName(), false,
                new Event.Error(response.code(), response.message()), null));
      }
    }

    @Override public void onFailure(Call<Article> call, Throwable t) {
      EventBus.getDefault()
          .post(new ItemDetailEvent(getClass().getSimpleName(), false,
              new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), null));
    }
  };

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

    mOnItemClickListener = new FeedListAdapter.OnFeedItemClickListener() {
      @Override public void onMentionedUserClick(FeedItem host) {
        if (PrefUtil.checkNetwork(getContext())) {
          Uri itemUri = Uri.parse(host.getMentionedObjectUrl());
          ApiClient.itemDetail(itemUri.getLastPathSegment()).enqueue(mOnArticleLoaded);
        }
      }

      @Override public void onItemContentClick(FeedItem item) {
        if (PrefUtil.checkNetwork(getContext())) {
          startActivity(
              ItemDetailActivity.createIntent(getContext(), item.getMentionedObjectUuid()));
        }
      }
    };

    mAdapter.setOnItemClickListener(mOnItemClickListener);
  }

  @Override public void onDestroyView() {
    mOnArticleLoaded = null;
    mOnItemClickListener = null;
    super.onDestroyView();
  }

  @SuppressWarnings("unused") public void onEventMainThread(ItemDetailEvent event) {
    Article article = event.article;
    if (article != null && article.getUser() != null) {
      startActivity(ProfileActivity.createIntent(getContext(), article.getUser().getId()));
    }
  }

  @NonNull @Override protected RealmListAdapter<FeedItem> createRealmAdapter() {
    RealmResults<FeedItem> items =
        mRealm.where(FeedItem.class).findAllSorted("createdAtInUnixtime", Sort.DESCENDING);
    return new FeedListAdapter(items);
  }

  @Override public void onFailure(Call<List<FeedItem>> call, Throwable t) {
    super.onFailure(call, t);
    EventBus.getDefault()
        .post(new ItemsEvent(eventTag(), false,
            new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), 1));
  }

  @Override public void onResponse(Call<List<FeedItem>> call, Response<List<FeedItem>> response) {
    if (response.code() != 200) {
      EventBus.getDefault()
          .post(new ItemsEvent(eventTag(), false,
              new Event.Error(response.code(), ApiClient.parseError(response).message), 1));
    } else {
      final List<FeedItem> items = response.body();
      if (!UIUtil.isEmpty(items)) {
        mTransactionTask = Attiq.realm().executeTransaction(new Realm.Transaction() {
          @Override public void execute(Realm realm) {
            for (FeedItem item : items) {
              item.setId(IOUtil.hashCode(item));
            }
            realm.copyToRealmOrUpdate(items);
          }
        }, new Realm.Transaction.Callback() {
          @Override public void onSuccess() {
            EventBus.getDefault().post(new ItemsEvent(eventTag(), true, null, mPage));
          }

          @Override public void onError(Exception e) {
            EventBus.getDefault()
                .post(new ItemsEvent(eventTag(), false,
                    new Event.Error(Event.Error.ERROR_UNKNOWN, e.getLocalizedMessage()), mPage));
          }
        });
      }
    }
  }

}
