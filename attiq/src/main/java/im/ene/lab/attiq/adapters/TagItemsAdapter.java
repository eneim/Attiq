package im.ene.lab.attiq.adapters;

import android.support.annotation.Nullable;
import android.view.View;

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

  public static class ViewHolder extends ArticleListAdapter.ViewHolder {

    public ViewHolder(View view) {
      super(view);
    }
  }
}
