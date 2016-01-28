package im.ene.lab.attiq.ui.adapters;

import android.support.annotation.Nullable;

import retrofit2.Callback;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
public abstract class ListAdapter<T> extends BaseListAdapter<T> {

  // indicate that this Adapter is calling API or not
  protected boolean isLoading = false;

  public abstract void loadItems(boolean isLoadingMore, int page, int pageLimit,
                                 @Nullable String query, Callback<List<T>> callback);

  public void addItem(T item) {
  }

  public void addItems(List<T> items) {
  }

  public void clear() {
  }

  public boolean isLoading() {
    return isLoading;
  }
}
