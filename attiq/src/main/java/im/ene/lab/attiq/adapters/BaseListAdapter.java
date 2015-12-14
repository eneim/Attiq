package im.ene.lab.attiq.adapters;

/**
 * Created by eneim on 12/13/15.
 */
public abstract class BaseListAdapter<T>
    extends BaseRecyclerAdapter<BaseRecyclerAdapter.ViewHolder<T>> implements ListAdapter<T> {

  @Override public void onBindViewHolder(ViewHolder<T> holder, int position) {
    T item = getItem(position);
    if (item != null) {
      holder.bindInternal(item);
    }
  }
}