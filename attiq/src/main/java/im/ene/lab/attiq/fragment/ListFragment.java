package im.ene.lab.attiq.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.adapters.ListAdapter;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.TypedEvent;
import im.ene.lab.attiq.widgets.EndlessScrollListener;
import im.ene.lab.attiq.widgets.MultiSwipeRefreshLayout;
import im.ene.lab.attiq.widgets.NonEmptyRecyclerView;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * Created by eneim on 1/6/16.
 */
public abstract class ListFragment<E>
    extends BaseFragment
    implements SwipeRefreshLayout.OnRefreshListener, Handler.Callback, Callback<List<E>> {

  private static final String TAG = "ListFragment";
  /**
   * Message sent from #onChange, used to update current list by change event from Realm
   */
  private static final int MESSAGE_UPDATE_DATA = 1 << 1;

  /**
   * Message sent from anywhere we want to load/reload data (refresh to reload, or scroll down to
   * load more items)
   */
  protected static final int MESSAGE_LOAD_RELOAD = 1 << 2;

  /**
   * Default item count per page
   */
  private static final int DEFAULT_THRESHOLD = ApiClient.DEFAULT_PAGE_LIMIT;

  /**
   * Default first page for API call
   */
  private static final int DEFAULT_FIRST_PAGE = 1;

  // In my experience, GridLayout provide more accurate Cell's measurement. It may have worse
  // performance (comparing to its super class LinearLayoutManager), but this a reasonable trade
  // off
  protected GridLayoutManager mLayoutManager;
  /**
   * UI components
   */
  @Bind(R.id.recycler_view) NonEmptyRecyclerView mRecyclerView;
  @Bind(R.id.swipe_refresh_layout) MultiSwipeRefreshLayout mSwipeRefreshLayout;
  @Bind(R.id.loading_container) View mLoadingView;
  @Bind(R.id.view_empty) View mEmptyView;
  @Bind(R.id.view_error) TextView mErrorView;

  protected ListAdapter<E> mAdapter;

  // User a handler to prevent too frequently calling of methods. For example Realm may trigger
  // #onChange a lot of time, since it doesn't support type-specific change event now. So we
  // should queue the Change event up, and remove the duplicated ones to save resources
  private Handler mHandler = new Handler(this);

  // This object requires a LayoutManager, so it must be initialized after we create our
  // LayoutManager.
  private EndlessScrollListener mEndlessScrollListener;

  private int mPage = DEFAULT_FIRST_PAGE;

  @Override public boolean handleMessage(Message msg) {
    // TODO consider using switch cases if we have more Messages to handle
    if (msg.what == MESSAGE_UPDATE_DATA) {
      mAdapter.notifyDataSetChanged();
      return true;
    } else if (msg.what == MESSAGE_LOAD_RELOAD) {
      loadReload();
      return true;
    }

    return false;
  }

  protected void loadReload() {
    boolean isRefreshing = mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing();
    if (isRefreshing) {
      mAdapter.clear();
    }

    boolean isLoadingMore = mAdapter.getItemCount() > 0 && !isRefreshing;
    if (isLoadingMore) {
      mPage++;
    } else if (isRefreshing) {
      mPage = DEFAULT_FIRST_PAGE;
    }

    // Show loading view only when we don't have any item and we want to renew the list
    if (isRefreshing && mAdapter.getItemCount() == 0) {
      mLoadingView.setVisibility(View.VISIBLE);
    }

    mAdapter.loadItems(isLoadingMore, mPage, DEFAULT_THRESHOLD, null, this);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_general_recycler_view, container, false);
  }

  @Override public void onDetach() {
    super.onDetach();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mLayoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
    mEndlessScrollListener = new EndlessScrollListener(mLayoutManager, DEFAULT_THRESHOLD) {
      @Override protected void loadMore() {
        mHandler.removeMessages(MESSAGE_LOAD_RELOAD);
        mHandler.sendEmptyMessageDelayed(MESSAGE_LOAD_RELOAD, 250);
      }
    };

    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.addOnScrollListener(mEndlessScrollListener);

    mSwipeRefreshLayout.setSwipeableChildren(
        mRecyclerView.getId(), mEmptyView.getId(), mErrorView.getId());
    mSwipeRefreshLayout.setOnRefreshListener(this);

    mAdapter = createAdapter();
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setErrorView(mErrorView);
    mRecyclerView.setEmptyView(mEmptyView);

    mHandler.removeMessages(MESSAGE_LOAD_RELOAD);
    mHandler.sendEmptyMessageDelayed(MESSAGE_LOAD_RELOAD, 250);
  }

  @Override public void onDestroyView() {
    mRecyclerView.removeOnScrollListener(mEndlessScrollListener);
    mEndlessScrollListener = null;
    mHandler.removeCallbacksAndMessages(null);
    super.onDestroyView();
  }

  @NonNull
  protected abstract ListAdapter<E> createAdapter();

  @Override public void onRefresh() {
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(true);
      mHandler.removeMessages(MESSAGE_LOAD_RELOAD);
      mHandler.sendEmptyMessageDelayed(MESSAGE_LOAD_RELOAD, 200);
    }
  }

  // Just do nothing here
  @SuppressWarnings("unused")
  public void onEventMainThread(TypedEvent<E> event) {
    Event.Error error = event.error;
    if (error != null && !UIUtil.isEmpty(error.message) && mErrorView != null) {
      mErrorView.setText(error.message);
    }

    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(false);
    }

    if (mRecyclerView != null) {
      mRecyclerView.setErrorViewShown(event.error != null);
    }

    if (mLoadingView != null) {
      mLoadingView.setVisibility(View.GONE);
    }
  }

  @Override public void onResponse(Response<List<E>> response) {
    Log.d(getClass().getSimpleName(),
        "onResponse() called with: " + "response = [" + response + "]");
    if (response.code() != 200) {
      EventBus.getDefault().post(new TypedEvent<>(false,
          new Event.Error(response.code(), ApiClient.parseError(response).message), null, mPage));
    } else {
      List<E> items = response.body();
      if (!UIUtil.isEmpty(items)) {
        mAdapter.addItems(items);
        EventBus.getDefault().post(new TypedEvent<>(true, null, items.get(0), mPage));
      }
    }
  }

  @Override public void onFailure(Throwable t) {
    Log.d(getClass().getSimpleName(), "onFailure() called with: " + "t = [" + t + "]");
    EventBus.getDefault().post(new TypedEvent<>(false,
        new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), null, mPage));
  }
}
