package im.ene.lab.attiq.ui.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.model.one.PublicTag;
import im.ene.lab.attiq.ui.widgets.RoundedTransformation;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 1/10/16.
 */
public class UserTagsAdapter extends ListAdapter<PublicTag> {

  private final Object LOCK = new Object();

  private final String mUserId;
  private final ArrayList<PublicTag> mItems;

  public UserTagsAdapter(String mUserId) {
    super();
    this.mUserId = mUserId;
    mItems = new ArrayList<>();
  }

  public UserTagsAdapter(String mUserId, ArrayList<PublicTag> items) {
    this.mUserId = mUserId;
    this.mItems = items;
  }

  private void cleanup(boolean shouldCleanup) {
    if (shouldCleanup) {
      synchronized (LOCK) {
        mItems.clear();
        notifyDataSetChanged();
      }
    }
  }

  @Override
  public void loadItems(boolean isLoadingMore, int page, int pageLimit, @Nullable String query,
                        final Callback<List<PublicTag>> callback) {
    cleanup(!isLoadingMore);
    isLoading = true;
    ApiClient.userFollowingTagsV1(mUserId, page, pageLimit).enqueue(
        new Callback<List<PublicTag>>() {
          @Override public void onResponse(Response<List<PublicTag>> response) {
            isLoading = false;
            if (callback != null) {
              callback.onResponse(response);
            }
          }

          @Override public void onFailure(Throwable t) {
            isLoading = false;
            if (callback != null) {
              callback.onFailure(t);
            }
          }
        });
  }

  @Override public void addItem(PublicTag item) {
    synchronized (LOCK) {
      mItems.add(item);
      notifyItemInserted(getItemCount() - 1);
    }
  }

  @Override public void addItems(List<PublicTag> items) {
    synchronized (LOCK) {
      int oldLen = getItemCount();
      mItems.addAll(items);
      notifyItemRangeInserted(oldLen, items.size());
    }
  }

  @Override public void clear() {
    synchronized (LOCK) {
      mItems.clear();
      notifyDataSetChanged();
    }
  }

  @Override public PublicTag getItem(int position) {
    return mItems.get(position);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
    final View tagView = LayoutInflater.from(parent.getContext())
        .inflate(ViewHolder.LAYOUT_RES, parent, false);
    final ViewHolder viewHolder = new ViewHolder(tagView);
    viewHolder.setOnViewHolderClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        int adapterPos = viewHolder.getAdapterPosition();
        if (mOnItemClickListener != null) {
          mOnItemClickListener.onItemClick(
              UserTagsAdapter.this, viewHolder, v, adapterPos, getItemId(adapterPos)
          );
        }
      }
    });

    return viewHolder;
  }

  @Override public int getItemCount() {
    return mItems.size();
  }

  public static class ViewHolder extends BaseListAdapter.ViewHolder<PublicTag> {

    private static final int LAYOUT_RES = R.layout.widget_tag_view_fixed;

    @Bind(R.id.tag_name) TextView mTagName;
    @Bind(R.id.tag_icon) ImageView mTagIcon;

    // Others
    @BindDimen(R.dimen.dimen_unit) int mIconBorderWidth;
    @BindColor(R.color.colorAccent) int mIconBorderColor;

    @BindDimen(R.dimen.tag_icon_size) int mTagIconSize;
    @BindDimen(R.dimen.tag_icon_size_half) int mTagIconSizeHalf;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
    }

    @Override public void bind(PublicTag item) {
      mTagName.setText(item.getName());
      Attiq.picasso().load(item.getIconUrl())
          .placeholder(R.drawable.ic_dnd_forwardslash_24dp)
          .error(R.drawable.ic_dnd_forwardslash_24dp)
          .resize(mTagIconSize, 0)
          .transform(new RoundedTransformation(
              mIconBorderWidth, mIconBorderColor, mTagIconSizeHalf))
          .into(mTagIcon);
    }
  }

  public static abstract class OnTagClickListener implements OnItemClickListener {

    public abstract void onTagClick(String tagName);

    @Override
    public void onItemClick(BaseAdapter adapter, BaseAdapter.ViewHolder viewHolder,
                            View view, int adapterPosition, long itemId) {
      final PublicTag item;
      if (adapter instanceof BaseListAdapter) {
        item = (PublicTag) ((BaseListAdapter) adapter).getItem(adapterPosition);
      } else {
        item = null;
      }

      if (item != null && view == viewHolder.itemView) {
        onTagClick(item.getUrlName());
      }
    }
  }
}
