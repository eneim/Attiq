package im.ene.lab.attiq.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import im.ene.lab.attiq.R;
import im.ene.lab.attiq.adapters.BaseListAdapter;
import im.ene.lab.attiq.adapters.ListAdapter;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.zero.FeedItem;
import im.ene.lab.attiq.fragment.ListFragment;
import im.ene.lab.attiq.util.event.TypedEvent;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class TestActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.container, TestListFragment.newInstance()).commit();
    }
  }

  public static class TestListFragment extends ListFragment<FeedItem> {

    private Handler mHandler = new Handler(this);

    public static TestListFragment newInstance() {
      return new TestListFragment();
    }

    @NonNull @Override protected ListAdapter<FeedItem> createAdapter() {
      return new TestListAdapter();
    }

    @Override public void onResponse(Response<List<FeedItem>> response) {
      super.onResponse(response);
    }

    @Override public void onEventMainThread(TypedEvent<FeedItem> event) {
      super.onEventMainThread(event);
    }
  }

  private static class TestListAdapter extends ListAdapter<FeedItem> {

    private final SortedList<FeedItem> mItems;

    public TestListAdapter() {
      this.mItems = new SortedList<>(FeedItem.class, new SortedListAdapterCallback<FeedItem>(this) {
        @Override public int compare(FeedItem o1, FeedItem o2) {
          return testString(o1).hashCode() - testString(o2).hashCode();
        }

        @Override public boolean areContentsTheSame(FeedItem oldItem, FeedItem newItem) {
          return testString(oldItem).equals(testString(newItem));
        }

        @Override public boolean areItemsTheSame(FeedItem item1, FeedItem item2) {
          return testString(item1).equals(testString(item2));
        }
      });
    }

    @Override
    public void loadItems(boolean isLoadingMore, int page, int pageLimit, @Nullable String query,
                          Callback<List<FeedItem>> callback) {
      final Long createdAt;
      if (getItemCount() == 0) {
        createdAt = null;
      } else {
        createdAt = getItem(getItemCount() - 1).getCreatedAtInUnixtime();
      }

      ApiClient.feed(createdAt).enqueue(callback);
    }

    @Override public void addItem(FeedItem item) {
      mItems.add(item);
    }

    @Override public void addItems(List<FeedItem> items) {
      mItems.addAll(items);
    }

    @Override public void clear() {
      mItems.clear();
      notifyDataSetChanged();
    }

    @Override public FeedItem getItem(int position) {
      return mItems.get(position);
    }

    @Override
    public ViewHolder<FeedItem> onCreateViewHolder(ViewGroup parent, int viewType) {
      TextView view = (TextView) LayoutInflater.from(parent.getContext())
          .inflate(TestViewHolder.LAYOUT_RES, parent, false);
      return new TestViewHolder(view);
    }

    @Override public int getItemCount() {
      return mItems.size();
    }
  }

  private static class TestViewHolder extends BaseListAdapter.ViewHolder<FeedItem> {

    static final int LAYOUT_RES = android.R.layout.simple_list_item_1;

    public TestViewHolder(@NonNull View itemView) {
      super(itemView);
    }

    @Override public void bind(FeedItem item) {
      ((TextView) itemView).setText(testString(item));
    }
  }

  static String testString(FeedItem item) {
    return item.getTrackableType() + " | " + item.getFollowableType() + "";
  }
}
