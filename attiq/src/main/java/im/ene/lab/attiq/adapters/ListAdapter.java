package im.ene.lab.attiq.adapters;

import android.support.annotation.Nullable;

import retrofit.Callback;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
public interface ListAdapter<T> {

  T getItem(int position);

  long getItemId(int position);

  void loadItems(boolean isLoadingMore, int page, int pageLimit, @Nullable String query,
                 Callback<List<T>> callback);
}
