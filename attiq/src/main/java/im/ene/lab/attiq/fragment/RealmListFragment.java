package im.ene.lab.attiq.fragment;

import android.content.Context;
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
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.adapters.RealmListAdapter;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.TypedEvent;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.EndlessScrollListener;
import im.ene.lab.attiq.widgets.MultiSwipeRefreshLayout;
import im.ene.lab.attiq.widgets.NonEmptyRecyclerView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
public abstract class RealmListFragment<E extends RealmObject>
    extends BaseFragment
    implements SwipeRefreshLayout.OnRefreshListener, Handler.Callback, Callback<List<E>> {

  private static final String TAG = "RealmListFragment";
  /**
   * Message sent from #onChange, used to update current list by change event from Realm
   */
  private static final int MESSAGE_UPDATE_DATA = 1 << 1;

  /**
   * Message sent from anywhere we want to load/reload data (refresh to reload, or scroll down to
   * load more items)
   */
  private static final int MESSAGE_LOAD_RELOAD = 1 << 2;

  /**
   * Default item count per page
   */
  private static final int DEFAULT_THRESHOLD = 20;

  /**
   * Default first page for API call
   */
  private static final int DEFAULT_FIRST_PAGE = 1;

  protected Realm mRealm;

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

  protected RealmListAdapter<E> mAdapter;

  // User a handler to prevent too frequently calling of methods. For example Realm may trigger
  // #onChange a lot of time, since it doesn't support type-specific change event now. So we
  // should queue the Change event up, and remove the duplicated ones to save resources
  private Handler mHandler = new Handler(this);

  private RealmChangeListener mDataChangeListener = new RealmChangeListener() {
    @Override public void onChange() {
      mHandler.removeMessages(MESSAGE_UPDATE_DATA);
      mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_DATA, 200);
    }
  };
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

  private void loadReload() {
    boolean isRefreshing = mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing();
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

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    mRealm = Attiq.realm();
    mRealm.addChangeListener(mDataChangeListener);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.layout_general_recycler_view, container, false);
  }

  @Override public void onResume() {
    super.onResume();
    mHandler.removeMessages(MESSAGE_LOAD_RELOAD);
    mHandler.sendEmptyMessageDelayed(MESSAGE_LOAD_RELOAD, 250);
  }

  @Override public void onDetach() {
    if (mRealm != null) {
      mRealm.removeChangeListener(mDataChangeListener);
      mRealm.close();
    }
    mDataChangeListener = null;
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
  }

  @Override public void onDestroyView() {
    mRecyclerView.removeOnScrollListener(mEndlessScrollListener);
    mEndlessScrollListener = null;
    super.onDestroyView();
  }

  @NonNull
  protected abstract RealmListAdapter<E> createAdapter();

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
  }

  @Override public void onResponse(Response<List<E>> response) {
    Log.d(TAG, "onResponse() called with: " + "response = [" + response + "]");
    if (response.code() != 200) {
      EventBus.getDefault().post(new TypedEvent<>(false,
          new Event.Error(response.code(), response.message()), null, mPage));
    } else {
      List<E> items = response.body();
      if (!UIUtil.isEmpty(items)) {
        Realm realm = Attiq.realm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(items);
        realm.commitTransaction();
        realm.close();
        EventBus.getDefault().post(new TypedEvent<>(true, null, items.get(0), mPage));
      }
    }

    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(false);
    }

    if (mRecyclerView != null) {
      mRecyclerView.setErrorViewShown(response.code() != 200);
    }

    if (mLoadingView != null) {
      mLoadingView.setVisibility(View.GONE);
    }
  }

  @Override public void onFailure(Throwable t) {
    Log.d(TAG, "onFailure() called with: " + "t = [" + t + "]");
    EventBus.getDefault().post(new TypedEvent<>(false,
        new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), null, mPage));

    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(false);
    }

    if (mRecyclerView != null) {
      mRecyclerView.setErrorViewShown(true);
    }

    if (mLoadingView != null) {
      mLoadingView.setVisibility(View.GONE);
    }
  }
}
