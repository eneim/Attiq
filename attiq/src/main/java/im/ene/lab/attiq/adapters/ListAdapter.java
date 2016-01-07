package im.ene.lab.attiq.adapters;

import android.support.annotation.Nullable;

import retrofit2.Callback;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
public abstract class ListAdapter<T> extends BaseListAdapter<T> {

  public abstract void loadItems(boolean isLoadingMore, int page, int pageLimit,
                                 @Nullable String query, Callback<List<T>> callback);

  public abstract void addItem(T item);

  public abstract void addItems(List<T> items);

}