package im.ene.lab.attiq.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.adapters.AttiqListAdapter;
import im.ene.lab.attiq.adapters.FeedAdapter;
import im.ene.lab.attiq.data.api.open.FeedItem;
import im.ene.lab.attiq.data.event.Event;
import im.ene.lab.attiq.data.event.TypedEvent;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.DividerItemDecoration;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit.Response;
import retrofit.Retrofit;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedListFragment extends RealmListFragment<FeedItem> {

  public FeedListFragment() {

  }

  public static FeedListFragment newInstance() {
    return new FeedListFragment();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
        DividerItemDecoration.VERTICAL_LIST));
  }

  @NonNull @Override protected AttiqListAdapter<FeedItem> createAdapter() {
    RealmResults<FeedItem> items = mRealm.where(FeedItem.class)
        .findAllSorted("createdAtInUnixtime", Sort.DESCENDING);
    return new FeedAdapter(items);
  }

  @Override public void onFailure(Throwable t) {
    super.onFailure(t);
  }

  @Override public void onResponse(Response<List<FeedItem>> response, Retrofit retrofit) {
    if (response.code() != 200) {
      EventBus.getDefault().post(new TypedEvent<>(false,
          new Event.Error(response.code(), response.message()), null, 1));
    } else {
      List<FeedItem> items = response.body();
      if (!UIUtil.isEmpty(items)) {
        for (FeedItem item : items) {
          if (item.getMentionedObjectUuid() == null) {
            try {
              item.setMentionedObjectUuid(IOUtil.sha1(IOUtil.toString(item)));
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
              e.printStackTrace();
            }
          }
        }

        Realm realm = Attiq.realm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(items);
        realm.commitTransaction();
        realm.close();
        EventBus.getDefault().post(new TypedEvent<>(true, null, items.get(0), 1));
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
}