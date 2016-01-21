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

package im.ene.lab.attiq.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;

import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.activities.ItemDetailActivity;
import im.ene.lab.attiq.activities.ProfileActivity;
import im.ene.lab.attiq.adapters.BaseAdapter;
import im.ene.lab.attiq.adapters.FeedListAdapter2;
import im.ene.lab.attiq.adapters.RealmListAdapter;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.two.Article;
import im.ene.lab.attiq.data.zero.FeedItem;
import im.ene.lab.attiq.util.AnalyticsTrackers;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ItemDetailEvent;
import im.ene.lab.attiq.util.event.TypedEvent;
import im.ene.lab.attiq.widgets.DividerItemDecoration;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedListFragment2 extends RealmListFragment<FeedItem> {

  private static final String SCREEN_NAME = "attiq:home:feed_list";

  public FeedListFragment2() {

  }

  @Override protected void onVisibilityChange(boolean isVisibleToUser) {
    super.onVisibilityChange(isVisibleToUser);
    if (isVisibleToUser) {
      AnalyticsTrackers.sendScreenView(SCREEN_NAME);
    }
  }

  public static FeedListFragment2 newInstance() {
    return new FeedListFragment2();
  }

  private static final String TAG = "FeedListFragment";

  private BaseAdapter.OnItemClickListener mOnItemClickListener;

  private MoPubRecyclerAdapter mMopubAdapter;

  private Callback<Article> mOnArticleLoaded = new Callback<Article>() {
    @Override public void onResponse(Response<Article> response) {
      Article article = response.body();
      if (article != null) {
        EventBus.getDefault().post(new ItemDetailEvent(true, null, article));
      } else {
        EventBus.getDefault().post(new ItemDetailEvent(false,
            new Event.Error(response.code(), response.message()), null));
      }
    }

    @Override public void onFailure(Throwable t) {
      EventBus.getDefault().post(new ItemDetailEvent(false,
          new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), null));
    }
  };

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

    mOnItemClickListener = new FeedListAdapter2.OnFeedItemClickListener() {
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
    return new FeedListAdapter2(items);
  }

  @Override public void onFailure(Throwable t) {
    super.onFailure(t);
    EventBus.getDefault().post(new TypedEvent<>(false,
        new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), null, 1));
  }

  @Override public void onResponse(Response<List<FeedItem>> response) {
    if (response.code() != 200) {
      EventBus.getDefault().post(new TypedEvent<>(false,
          new Event.Error(response.code(), ApiClient.parseError(response).message), null, 1));
    } else {
      final List<FeedItem> items = response.body();
      if (!UIUtil.isEmpty(items)) {
        mTransactionTask = Attiq.realm().executeTransaction(new Realm.Transaction() {
          @Override public void execute(Realm realm) {
            try {
              for (FeedItem item : items) {
                item.setId(IOUtil.sha1(IOUtil.toString(item)));
                realm.copyToRealmOrUpdate(item);
              }
            } catch (NoSuchAlgorithmException e) {
              e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
              e.printStackTrace();
            }
          }
        }, new Realm.Transaction.Callback() {

          @Override public void onSuccess() {
            EventBus.getDefault().post(new TypedEvent<>(true, null, items.get(0), mPage));
          }

          @Override public void onError(Exception e) {
            EventBus.getDefault().post(new TypedEvent<>(false,
                new Event.Error(Event.Error.ERROR_UNKNOWN, e.getLocalizedMessage()), null, mPage));
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
}
