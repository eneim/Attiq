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
import im.ene.lab.attiq.adapters.FeedListAdapter;
import im.ene.lab.attiq.adapters.ListAdapter;
import im.ene.lab.attiq.adapters.OnItemClickListener;
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
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedListFragment extends ListFragment<FeedItem> {

  private static final String SCREEN_NAME = "attiq:home:feed_list";

  public FeedListFragment() {

  }

  @Override protected void onVisibilityChange(boolean isVisibleToUser) {
    super.onVisibilityChange(isVisibleToUser);
    if (isVisibleToUser) {
      AnalyticsTrackers.sendScreenView(SCREEN_NAME);
    }
  }

  public static FeedListFragment newInstance() {
    return new FeedListFragment();
  }

  private static final String TAG = "FeedListFragment";

  private OnItemClickListener mOnItemClickListener;

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

  @NonNull @Override protected ListAdapter<FeedItem> createAdapter() {
    return new FeedListAdapter();
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
      List<FeedItem> items = response.body();
      if (!UIUtil.isEmpty(items)) {
        final Realm realm = Attiq.realm();
        realm.beginTransaction();
        for (FeedItem item : items) {
          item.setId(IOUtil.hashCode(item));
          realm.copyToRealmOrUpdate(item);
        }
        realm.commitTransaction();
        mAdapter.addItems(items);
        EventBus.getDefault().post(new TypedEvent<>(true, null, items.get(0), 1));
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
