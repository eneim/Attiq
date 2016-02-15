package im.ene.lab.attiq.ui.adapters;

import android.support.annotation.Nullable;
import java.util.List;
import retrofit2.Callback;

/**
 * Created by eneim on 12/13/15.
 *
 * ListAdapter, customized for Attiq
 */
public abstract class AttiqListAdapter<T> extends BaseListAdapter<T> {

  // indicate that this Adapter is calling API or not
  protected boolean isLoading = false;

  /**
   * Request API call
   */
  public abstract void loadItems(boolean isLoadingMore, int page, int pageLimit,
      @Nullable String query, Callback<List<T>> callback);

  /**
   * Add new item to current Data list
   */
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
