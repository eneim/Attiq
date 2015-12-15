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

import butterknife.Bind;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.adapters.BaseListAdapter;
import im.ene.lab.attiq.data.event.EventWrapper;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.EndlessScrollListener;
import im.ene.lab.attiq.widgets.MultiSwipeRefreshLayout;
import im.ene.lab.attiq.widgets.NonEmptyRecyclerView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
public abstract class RealmListFragment<E extends RealmObject>
    extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, Callback<List<E>>,
    Handler.Callback {

  private static final String TAG = "RealmListFragment";
  /**
   *
   */
  private static final int MESSAGE_UPDATE_DATA = 0xffff;

  /**
   *
   */
  private static final int MESSAGE_LOAD_RELOAD = 0x0001;

  /**
   *
   */
  private static final int DEFAULT_THRESHOLD = 20;

  /**
   *
   */
  private static final int DEFAULT_FIRST_PAGE = 1;

  protected Realm mRealm;
  protected GridLayoutManager mLayoutManager;
  /**
   * UI components
   */
  @Bind(R.id.recycler_view) NonEmptyRecyclerView mRecyclerView;
  @Bind(R.id.swipe_refresh_layout) MultiSwipeRefreshLayout mSwipeRefreshLayout;
  @Bind(R.id.view_empty) View mEmptyView;
  @Bind(R.id.view_error) View mErrorView;
  /**
   * Logic components
   */
  protected BaseListAdapter<E> mAdapter;
  private Handler mHandler = new Handler(this);
  private RealmChangeListener mDataChangeListener = new RealmChangeListener() {
    @Override public void onChange() {
      mHandler.removeMessages(MESSAGE_UPDATE_DATA);
      mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_DATA, 200);
    }
  };
  private EndlessScrollListener mEndlessScrollListener;

  private int mPage = DEFAULT_FIRST_PAGE;

  @Override public boolean handleMessage(Message msg) {
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
    Log.d(TAG, "loadReload() called with: " + "");
    boolean isRefreshing = mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing();
    boolean isLoadingMore = mAdapter.getItemCount() > 0 && !isRefreshing;
    if (isLoadingMore) {
      mPage++;
    } else if (isRefreshing) {
      mPage = DEFAULT_FIRST_PAGE;
    }
    mAdapter.loadItems(isLoadingMore, mPage, DEFAULT_THRESHOLD, null, this);
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    mRealm = Attiq.realm();
    mRealm.addChangeListener(mDataChangeListener);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
      savedInstanceState) {
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
    mEndlessScrollListener = null;
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
    super.onDestroyView();
  }

  @NonNull
  protected abstract BaseListAdapter<E> createAdapter();

  @Override public void onRefresh() {
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(true);
      mHandler.removeMessages(MESSAGE_LOAD_RELOAD);
      mHandler.sendEmptyMessageDelayed(MESSAGE_LOAD_RELOAD, 200);
    }
  }

  @Override public void onResponse(Response<List<E>> response, Retrofit retrofit) {
    Log.d(TAG, "onResponse() called with: " + "response = [" + response + "]");

    List<E> items = response.body();
    if (!UIUtil.isEmpty(items)) {
      Realm realm = Attiq.realm();
      realm.beginTransaction();
      realm.copyToRealmOrUpdate(items);
      realm.commitTransaction();
      realm.close();
      mEventBus.post(new EventWrapper<>(true, items.get(0), mPage));
    }

    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(false);
    }

    if (mRecyclerView != null) {
      mRecyclerView.setErrorViewShown(response.code() != 200);
    }
  }

  @Override public void onFailure(Throwable t) {
    Log.d(TAG, "onFailure() called with: " + "t = [" + t + "]");
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(false);
    }

    if (mRecyclerView != null) {
      mRecyclerView.setErrorViewShown(true);
    }
  }

  public abstract void onEventMainThread(EventWrapper<E> event);
}
