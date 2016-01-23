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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.ui.adapters.ListAdapter;
import im.ene.lab.attiq.ui.adapters.RealmListAdapter;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.TypedEvent;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
public abstract class RealmListFragment<E extends RealmObject>
    extends ListFragment<E>
    implements SwipeRefreshLayout.OnRefreshListener, Handler.Callback, Callback<List<E>> {

  private static final String TAG = "RealmListFragment";

  protected Realm mRealm;
  protected RealmAsyncTask mTransactionTask;

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

  @Override
  protected void loadReload() {
    boolean isRefreshing = mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing();
    boolean isLoadingMore = !isRefreshing;
    if (isLoadingMore) {
      mPage++;
    } else {
      mPage = DEFAULT_FIRST_PAGE;
    }

    // Show loading view only when we don't have any item and we want to renew the list
    if (isRefreshing && mAdapter.getItemCount() == 0) {
      mLoadingView.setVisibility(View.VISIBLE);
    }

    mAdapter.loadItems(isLoadingMore, mPage, DEFAULT_THRESHOLD, null, this);
  }

  @NonNull @Override protected ListAdapter<E> createAdapter() {
    return createRealmAdapter();
  }

  @NonNull
  protected abstract RealmListAdapter<E> createRealmAdapter();

  @Override public void onRefresh() {
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(true);
      mHandler.removeMessages(MESSAGE_LOAD_RELOAD);
      mHandler.sendEmptyMessageDelayed(MESSAGE_LOAD_RELOAD, 200);
    }
  }

  @Override public void onResponse(Response<List<E>> response) {
    Log.d(getClass().getSimpleName(),
        "onResponse() called with: " + "response = [" + response + "]");
    if (response.code() != 200) {
      EventBus.getDefault().post(new TypedEvent<>(false,
          new Event.Error(response.code(), ApiClient.parseError(response).message), null, mPage));
    } else {
      final List<E> items = response.body();
      if (!UIUtil.isEmpty(items)) {
        mTransactionTask = Attiq.realm().executeTransaction(new Realm.Transaction() {
          @Override public void execute(Realm realm) {
            realm.copyToRealmOrUpdate(items);
          }
        }, new Realm.Transaction.Callback() {
          @Override public void onSuccess() {
            super.onSuccess();
            EventBus.getDefault().post(new TypedEvent<>(true, null, items.get(0), mPage));
          }

          @Override public void onError(Exception e) {
            super.onError(e);
            EventBus.getDefault().post(new TypedEvent<>(false,
                new Event.Error(Event.Error.ERROR_UNKNOWN, e.getLocalizedMessage()), null, mPage));
          }
        });
      }
    }
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    mRealm = Attiq.realm();
    mRealm.addChangeListener(mDataChangeListener);
  }

  @Override public void onDetach() {
    if (mRealm != null) {
      mRealm.removeChangeListener(mDataChangeListener);
      mRealm.close();
    }
    mDataChangeListener = null;
    super.onDetach();
  }

  @Override public void onPause() {
    super.onPause();
    if (mTransactionTask != null && !mTransactionTask.isCancelled()) {
      mTransactionTask.cancel();
    }
  }

}
