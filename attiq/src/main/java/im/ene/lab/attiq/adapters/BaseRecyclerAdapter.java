package im.ene.lab.attiq.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by eneim on 12/13/15.
 */
public abstract class BaseRecyclerAdapter<VH extends BaseRecyclerAdapter.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  public static abstract class ViewHolder<T> extends RecyclerView.ViewHolder {

    protected final Context mContext;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      mContext = itemView.getContext();
    }

    protected T mItem;

    void bindInternal(T item) {
      this.mItem = item;
      bind(item);
    }

    public abstract void bind(T item);
  }

}
