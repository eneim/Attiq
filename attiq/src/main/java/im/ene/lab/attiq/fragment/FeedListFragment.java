package im.ene.lab.attiq.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.activities.ItemDetailActivity;
import im.ene.lab.attiq.activities.ProfileActivity;
import im.ene.lab.attiq.adapters.BaseAdapter;
import im.ene.lab.attiq.adapters.FeedListAdapter;
import im.ene.lab.attiq.adapters.ListAdapter;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.two.Article;
import im.ene.lab.attiq.data.zero.FeedItem;
import im.ene.lab.attiq.util.AnalyticsTrackers;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ItemDetailEvent;
import im.ene.lab.attiq.util.event.TypedEvent;
import im.ene.lab.attiq.widgets.DividerItemDecoration;
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

  private BaseAdapter.OnItemClickListener mOnItemClickListener;

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

  @Override public void onDestroyView() {
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
        mAdapter.addItems(items);
        EventBus.getDefault().post(new TypedEvent<>(true, null, items.get(0), 1));
      }
    }
  }
}
