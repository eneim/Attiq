package im.ene.lab.attiq.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.model.one.PublicTag;
import im.ene.lab.attiq.ui.activities.TagItemsActivity;
import im.ene.lab.attiq.ui.adapters.ListAdapter;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.UserTagsAdapter;
import im.ene.lab.attiq.util.AnalyticsUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ItemsEvent;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eneim on 1/10/16.
 */
public class UserTagsFragment extends BaseFragment
    implements Handler.Callback, Callback<List<PublicTag>> {

  private static final String SCREEN_NAME = "attiq:user:tags";

  protected static final String ARGS_USER_ID = "attiq_fragment_args_user_id";
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
  private static final int DEFAULT_THRESHOLD = ApiClient.DEFAULT_PAGE_LIMIT;
  /**
   * Default first page for API call
   */
  private static final int DEFAULT_FIRST_PAGE = 1;
  private static final String TAG = "UserTagsFragment";
  private final State mState = new State();
  protected String mUserId;
  @Bind(R.id.tags) RecyclerView mRecyclerView;
  private ListAdapter<PublicTag> mAdapter;
  // User a handler to prevent too frequently calling of methods. For example Realm may trigger
  // #onChange a lot of time, since it doesn't support type-specific change event now. So we
  // should queue the Change event up, and remove the duplicated ones to save resources
  private Handler mHandler = new Handler(this);
  private int mPage = DEFAULT_FIRST_PAGE;

  private OnItemClickListener mOnItemClickListener;

  public static UserTagsFragment newInstance(String userId) {
    UserTagsFragment fragment = new UserTagsFragment();
    Bundle args = new Bundle();
    args.putString(ARGS_USER_ID, userId);
    fragment.setArguments(args);
    return fragment;
  }

  private ArrayList<PublicTag> mItems;

  @Override protected void onVisibilityChange(boolean isVisibleToUser) {
    super.onVisibilityChange(isVisibleToUser);
    if (isVisibleToUser) {
      AnalyticsUtil.sendScreenView(SCREEN_NAME);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    RecyclerView.LayoutManager layoutManager =
        new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
    mRecyclerView.setLayoutManager(layoutManager);

    mItems = new ArrayList<>();
    mAdapter = createAdapter();

    mOnItemClickListener = new UserTagsAdapter.OnTagClickListener() {
      @Override public void onTagClick(String tagName) {
        startActivity(TagItemsActivity.createIntent(getContext(), tagName));
      }
    };

    mAdapter.setOnItemClickListener(mOnItemClickListener);
    mRecyclerView.setAdapter(mAdapter);

    mHandler.removeMessages(MESSAGE_LOAD_RELOAD);
    mHandler.sendEmptyMessageDelayed(MESSAGE_LOAD_RELOAD, 250);
  }

  @NonNull protected ListAdapter<PublicTag> createAdapter() {
    return new UserTagsAdapter(mUserId, mItems);
  }

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
    boolean isLoadingMore = mAdapter.getItemCount() > 1;
    if (isLoadingMore) {
      mPage++;
    } else {
      mPage = DEFAULT_FIRST_PAGE;
    }

    mAdapter.loadItems(isLoadingMore, mPage, DEFAULT_THRESHOLD, null, this);
  }

  @Override public void onResponse(Call<List<PublicTag>> call, Response<List<PublicTag>> response) {
    Log.d(TAG, "onResponse() called with: " + "response = [" + response + "]");
    if (response.code() != 200) {
      mState.hasFollowingTags = false;
      EventBus.getDefault().post(new StateEvent(UserTagsFragment.class.getSimpleName(), false,
          new Event.Error(response.code(), response.message()), mState));
    } else {
      List<PublicTag> items = response.body();
      if (!UIUtil.isEmpty(items)) {
        mAdapter.addItems(items);
        mState.hasFollowingTags = true;
        EventBus.getDefault().post(new StateEvent(UserTagsFragment.class.getSimpleName(),
            true, null, mState));
      } else {
        mState.hasFollowingTags = false;
        EventBus.getDefault().post(new StateEvent(UserTagsFragment.class.getSimpleName(),
            true, null, mState));
      }
    }
  }

  @Override public void onFailure(Call<List<PublicTag>> call, Throwable t) {
    Log.d(TAG, "onFailure() called with: " + "t = [" + t + "]");
    EventBus.getDefault().post(new ItemsEvent(UserTagsFragment.class.getSimpleName(), false,
        new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), mPage));
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mUserId = getArguments().getString(ARGS_USER_ID);
      mState.userId = mUserId;
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
      savedInstanceState) {
    return inflater.inflate(R.layout.widget_user_infor_tags, container, false);
  }

  // Just do nothing here
  @SuppressWarnings("unused")
  public void onEventMainThread(StateEvent event) {
    if (mUserId.equals(event.state.userId) && getView() != null) {
      getView().setVisibility(event.state.hasFollowingTags ? View.VISIBLE : View.GONE);
    }
  }

  private static class State {

    private String userId;

    private boolean hasFollowingTags = false;
  }

  private static class StateEvent extends Event {

    private final State state;

    public StateEvent(@Nullable String tag, boolean success, @Nullable Error error, State state) {
      super(tag, success, error);
      this.state = state;
    }
  }
}
