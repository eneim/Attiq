package im.ene.lab.attiq.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.two.Article;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * Created by eneim on 1/6/16.
 */
public class UserArticlesAdapter extends ArticleListAdapter {

  private final String mUserId;

  public UserArticlesAdapter(String userId) {
    super();
    this.mUserId = userId;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = ArticleListAdapter.ViewHolder.createViewHolder(parent, viewType).itemView;
    final ViewHolder viewHolder = new ViewHolder(view);
    viewHolder.setOnViewHolderClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
          mOnItemClickListener.onItemClick(
              UserArticlesAdapter.this, viewHolder, view, position, getItemId(position)
          );
        }
      }
    });

    return viewHolder;
  }

  @Override
  public void loadItems(final boolean isLoadingMore, int page, int pageLimit,
                        @Nullable String query, final Callback<List<Article>> callback) {
    ApiClient.userItems(mUserId, page, pageLimit).enqueue(new Callback<List<Article>>() {
      @Override public void onResponse(Response<List<Article>> response) {
        cleanup(!isLoadingMore);
        if (callback != null) {
          callback.onResponse(response);
        }
      }

      @Override public void onFailure(Throwable throwable) {
        cleanup(!isLoadingMore);
        if (callback != null) {
          callback.onFailure(throwable);
        }
      }
    });
  }

  public static class ViewHolder extends ArticleListAdapter.ViewHolder {

    public ViewHolder(View view) {
      super(view);
    }
  }
}
