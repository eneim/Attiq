package im.ene.lab.attiq.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by eneim on 12/13/15.
 */
public abstract class BaseRecyclerAdapter<VH extends BaseRecyclerAdapter.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  public static abstract class ViewHolder<T> extends RecyclerView.ViewHolder {

    public ViewHolder(View itemView) {
      super(itemView);
    }

    protected T mItem;

    void bindInternal(T item) {
      this.mItem = item;
      bind(item);
    }

    public abstract void bind(T item);
  }

}
