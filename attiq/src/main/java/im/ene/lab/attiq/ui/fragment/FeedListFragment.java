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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.common.MoPub;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;

import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.zero.FeedItem;
import im.ene.lab.attiq.ui.activities.ItemDetailActivity;
import im.ene.lab.attiq.ui.activities.ProfileActivity;
import im.ene.lab.attiq.ui.adapters.FeedListAdapter;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.RealmListAdapter;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;
import im.ene.lab.attiq.util.AnalyticsUtil;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ItemDetailEvent;
import im.ene.lab.attiq.util.event.ItemsEvent;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedListFragment extends RealmListFragment<FeedItem> {

  private static final String SCREEN_NAME = "attiq:home:feed_list";

  public FeedListFragment() {

  }

  @Override protected void onVisibilityChange(boolean isVisibleToUser) {
    super.onVisibilityChange(isVisibleToUser);
    if (isVisibleToUser) {
      AnalyticsUtil.sendScreenView(SCREEN_NAME);
    }
  }

  public static FeedListFragment newInstance() {
    return new FeedListFragment();
  }

  private static final String TAG = "FeedListFragment";

  private OnItemClickListener mOnItemClickListener;

  private MoPubRecyclerAdapter mMopubAdapter;

  private Callback<Article> mOnArticleLoaded = new Callback<Article>() {
    @Override public void onResponse(Call<Article> call, Response<Article> response) {
      Article article = response.body();
      if (article != null) {
        EventBus.getDefault().post(
            new ItemDetailEvent(getClass().getSimpleName(), true, null, article));
      } else {
        EventBus.getDefault().post(new ItemDetailEvent(getClass().getSimpleName(), false,
            new Event.Error(response.code(), response.message()), null));
      }
    }

    @Override public void onFailure(Call<Article> call, Throwable t) {
      EventBus.getDefault().post(new ItemDetailEvent(getClass().getSimpleName(), false,
          new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), null));
    }
  };

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MoPub.setLocationAwareness(MoPub.LocationAwareness.NORMAL);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mMopubAdapter = new MoPubRecyclerAdapter(getActivity(), mAdapter);
    ViewBinder viewBinder = new ViewBinder.Builder(NativeAdsView.LAYOUT_RES)
        .titleId(NativeAdsView.AD_VIEW_TITLE)
        .iconImageId(NativeAdsView.AD_VIEW_ICON)
        .textId(NativeAdsView.AD_VIEW_TEXT)
        .mainImageId(NativeAdsView.AD_VIEW_IMAGE)
        .build();
    MoPubStaticNativeAdRenderer adViewRenderer = new MoPubStaticNativeAdRenderer(viewBinder);
    mMopubAdapter.registerAdRenderer(adViewRenderer);

    // replace by mMopubAdapter
    mRecyclerView.setAdapter(mMopubAdapter);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
        DividerItemDecoration.VERTICAL_LIST));

    mOnItemClickListener = new FeedListAdapter.OnFeedItemClickListener() {
      @Override public void onMentionedUserClick(FeedItem host) {
        Uri itemUri = Uri.parse(host.getMentionedObjectUrl());
        ApiClient.itemDetail(itemUri.getLastPathSegment()).enqueue(mOnArticleLoaded);
      }

      @Override public void onItemContentClick(FeedItem item) {
        startActivity(ItemDetailActivity.createIntent(getContext(), item.getMentionedObjectUuid()));
      }
    };

    mAdapter.setOnItemClickListener(mOnItemClickListener);
  }

  @Override public void onResume() {
    super.onResume();
    // Optional targeting parameters
    RequestParameters parameters = new RequestParameters.Builder()
        //.keywords("your target words here")
        .build();
    // Request ads when the user returns to this activity
    mMopubAdapter.loadAds(getString(R.string.attiq_mopub_add_id), parameters);
  }

  @Override public void onDestroyView() {
    mMopubAdapter.destroy();
    mOnArticleLoaded = null;
    mOnItemClickListener = null;
    super.onDestroyView();
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(ItemDetailEvent event) {
    Article article = event.article;
    if (article != null && article.getUser() != null) {
      startActivity(ProfileActivity.createIntent(getContext(), article.getUser().getId()));
    }
  }

  @NonNull @Override protected RealmListAdapter<FeedItem> createRealmAdapter() {
    RealmResults<FeedItem> items = mRealm.where(FeedItem.class)
        .findAllSorted("createdAtInUnixtime", Sort.DESCENDING);
    return new FeedListWithAdsAdapter(items);
  }

  @Override public void onFailure(Call<List<FeedItem>> call, Throwable t) {
    super.onFailure(call, t);
    EventBus.getDefault().post(new ItemsEvent(eventTag(), false,
        new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), 1));
  }

  @Override public void onResponse(Call<List<FeedItem>> call, Response<List<FeedItem>> response) {
    if (response.code() != 200) {
      EventBus.getDefault().post(new ItemsEvent(eventTag(), false,
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
            EventBus.getDefault().post(
                new ItemsEvent(eventTag(), true, null, mPage));
          }

          @Override public void onError(Exception e) {
            EventBus.getDefault().post(new ItemsEvent(eventTag(), false,
                new Event.Error(Event.Error.ERROR_UNKNOWN, e.getLocalizedMessage()), mPage));
          }
        });
      }
    }
  }

  /**
   * Native Ads on Public timeline
   */
  private static class NativeAdsView {
    private static final int LAYOUT_RES = R.layout.ads_item_view_public;

    private static final int AD_VIEW_TITLE = R.id.ads_title;
    private static final int AD_VIEW_ICON = R.id.ads_icon;
    private static final int AD_VIEW_IMAGE = R.id.ads_image;
    private static final int AD_VIEW_TEXT = R.id.ads_text;
  }

  private class FeedListWithAdsAdapter extends FeedListAdapter {

    public FeedListWithAdsAdapter(RealmResults<FeedItem> items) {
      super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      final ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
      viewHolder.setOnViewHolderClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          int position = viewHolder.getAdapterPosition();
          if (mMopubAdapter != null) {
            try {
              position = mMopubAdapter.getOriginalPosition(position);
              if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(FeedListWithAdsAdapter.this,
                    viewHolder, v, position, getItemId(position));
              }
            } catch (Exception er) {
              er.printStackTrace();
            }
          }
        }
      });
      return viewHolder;
    }
  }
}
