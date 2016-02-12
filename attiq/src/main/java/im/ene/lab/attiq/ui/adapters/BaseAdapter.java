package im.ene.lab.attiq.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by eneim on 12/15/15.
 * <p/>
 * A less abstract Adapter, to simplify RecyclerView#Adapter implementation
 */
public abstract class BaseAdapter<VH extends BaseAdapter.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  /**
   * This custom onClick event listener should be set by the Adapter
   */
  protected OnItemClickListener mOnItemClickListener;

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.mOnItemClickListener = onItemClickListener;
  }

  /**
   * An abstract ViewHolder
   */
  public abstract static class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      // This project use ButterKnife~
      ButterKnife.bind(this, itemView);
    }

    /**
     * This method is supposed to be override every time. For example, a view holder holds more
     * than 2 views, and Client want to listen to the click event on each of them.
     * <p/>
     * By default, the main #itemView will receive the click event.
     * <p/>
     * !IMPORTANT: This method is used optionally.
     *
     * @param listener to listen to Click event
     */
    // NOTE: Long name, I know
    public void setOnViewHolderClickListener(View.OnClickListener listener) {
      itemView.setOnClickListener(listener);
    }
  }

}
