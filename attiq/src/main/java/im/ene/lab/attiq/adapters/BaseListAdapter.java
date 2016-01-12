package im.ene.lab.attiq.adapters;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

/**
 * Created by eneim on 12/15/15.
 *
 * @param <T> This Adapter is specified to use with List of Objects of type T
 */
public abstract class BaseListAdapter<T> extends BaseAdapter<BaseListAdapter.ViewHolder<T>> {

  /**
   * This Adapter must support retrieving item(s)
   * <p/>
   * !IMPORTANT General Adapter could support various Types of Object, so we must not force it to
   * return a single Type of object. This BaseListAdapter was created to support those cases.
   *
   * @param position of the item we want to get
   * @return expected Item at a position
   */
  public abstract T getItem(int position);

  /**
   * If Client implement this method, He must call super.onBindViewHolder for expected
   * behaviors.
   *
   * @param holder
   * @param position
   */
  @CallSuper
  @Override public void onBindViewHolder(ViewHolder<T> holder, int position) {
    T item = getItem(position);
    if (item != null) {
      holder.bindInternal(item);
    }
  }

  /**
   * For now we don't support this method.
   *
   * @param holder
   * @param position
   * @param payloads
   */
  /*hide*/
  @Override
  public void onBindViewHolder(ViewHolder<T> holder, int position, List<Object> payloads) {
    super.onBindViewHolder(holder, position, null);
  }

  /**
   * General abstract ViewHolder to support specific Data type
   *
   * @param <T> expected Data Type
   */
  public abstract static class ViewHolder<T> extends BaseAdapter.ViewHolder {

    // I think it's not bad to have an shallow copy of current Data
    protected T mItem;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
    }

    // This method will always be called by Adapter
    void bindInternal(T item) {
      mItem = item;
      bind(item);
    }

    // Client then update its ViewHolder's appearance here
    public abstract void bind(T item);
  }
}
