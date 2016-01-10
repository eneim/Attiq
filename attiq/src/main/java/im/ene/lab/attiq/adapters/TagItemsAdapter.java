package im.ene.lab.attiq.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.two.Article;
import retrofit2.Callback;

import java.util.List;

/**
 * Created by eneim on 1/10/16.
 */
public class TagItemsAdapter extends ArticleListAdapter {

  private final String mTagId;

  public TagItemsAdapter(String tagId) {
    super();
    this.mTagId = tagId;
  }

  @Override
  public void loadItems(boolean isLoadingMore, int page, int pageLimit, @Nullable String query,
                        Callback<List<Article>> callback) {
    ApiClient.tagItems(mTagId, page, pageLimit).enqueue(callback);
  }

  @Override
  public ArticleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final ViewHolder viewHolder =
        new ViewHolder(super.onCreateViewHolder(parent, viewType).itemView);
    viewHolder.setOnViewHolderClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
          mOnItemClickListener.onItemClick(
              TagItemsAdapter.this, viewHolder, v, position, getItemId(position)
          );
        }
      }
    });

    return viewHolder;
  }

  public static class ViewHolder extends ArticleListAdapter.ViewHolder {

    public ViewHolder(View view) {
      super(view);
    }

    @Override public void setOnViewHolderClickListener(View.OnClickListener listener) {
      super.setOnViewHolderClickListener(listener);
      mItemUserImage.setOnClickListener(listener);
    }
  }
}
