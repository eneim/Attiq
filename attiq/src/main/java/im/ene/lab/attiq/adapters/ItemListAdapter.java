package im.ene.lab.attiq.adapters;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import im.ene.lab.attiq.data.ApiClient;
import im.ene.lab.attiq.data.response.Item;
import io.realm.RealmResults;
import retrofit.Callback;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
public class ItemListAdapter extends BaseListAdapter<Item> {

  private final RealmResults<Item> mItems;

  public ItemListAdapter(RealmResults<Item> items) {
    super();
    mItems = items;
  }

  @Override
  public ViewHolder<Item> onCreateViewHolder(ViewGroup parent, int viewType) {
    TextView view = (TextView) LayoutInflater.from(parent.getContext())
        .inflate(android.R.layout.simple_list_item_1, parent, false);
    return new ViewHolder<Item>(view) {
      @Override public void bind(Item item) {
        ((TextView) itemView).setText(item.getUrl());
      }
    };
  }

  @Override public int getItemCount() {
    return mItems.size();
  }

  @Override public Item getItem(int position) {
    return mItems.get(position);
  }

  @Override
  public void loadItems(boolean isLoadingMore, int page, int pageLimit, @Nullable String query,
                        Callback<List<Item>> callback) {
    ApiClient.items(page, pageLimit, query).enqueue(callback);
  }
}
