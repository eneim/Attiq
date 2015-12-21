package im.ene.lab.attiq.adapters;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

import butterknife.Bind;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.api.v2.response.Article;
import im.ene.lab.attiq.data.api.v2.response.ItemTag;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.RoundedTransformation;
import io.realm.RealmResults;
import retrofit.Callback;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
public class ItemListAdapter extends BaseListAdapter<Article> {

  private final RealmResults<Article> mArticles;

  public ItemListAdapter(RealmResults<Article> articles) {
    super();
    mArticles = articles;
    setHasStableIds(true);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(ViewHolder.LAYOUT_RES, parent, false);
    return new ViewHolder(view);
  }

  @Override public long getItemId(int position) {
    return getItem(position).getId().hashCode();
  }

  @Override public int getItemCount() {
    return mArticles.size();
  }

  @Override public Article getItem(int position) {
    return mArticles.get(position);
  }

  @Override
  public void loadItems(boolean isLoadingMore, int page, int pageLimit, @Nullable String query,
                        Callback<List<Article>> callback) {
    ApiClient.items(page, pageLimit, query).enqueue(callback);
  }

  public static class ViewHolder extends BaseRecyclerAdapter.ViewHolder<Article> {

    static final int LAYOUT_RES = R.layout.post_item_view;

    private final LayoutInflater mInflater;

    private final int mIconCornerRadius;
    private final int mIconBorderWidth;
    private final int mIconBorderColor;

    @Bind(R.id.item_user_icon) ImageView mItemUserImage;
    @Bind(R.id.item_title) TextView mItemTitle;
    @Bind(R.id.item_tags) FlowLayout mItemTags;
    @Bind(R.id.item_info) TextView mItemInfo;

    public ViewHolder(View view) {
      super(view);
      mInflater = LayoutInflater.from(mContext);
      mIconCornerRadius = mContext.getResources()
          .getDimensionPixelSize(R.dimen.item_icon_size_half);
      mIconBorderWidth = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_unit);
      mIconBorderColor = UIUtil.getColor(mContext, R.color.colorPrimary);
    }

    @Override public void bind(Article article) {
      mItemInfo.setVisibility(View.GONE);

      mItemTitle.setText(article.getTitle());
      if (!UIUtil.isEmpty(article.getUser().getProfileImageUrl())) {
        mItemUserImage.setVisibility(View.VISIBLE);
        Attiq.picasso().load(article.getUser().getProfileImageUrl())
            .fit().centerInside()
            .transform(new RoundedTransformation(
                mIconBorderWidth, mIconBorderColor, mIconCornerRadius))
            .into(mItemUserImage);
      } else {
        mItemUserImage.setVisibility(View.GONE);
      }

      mItemTags.removeAllViews();
      if (!UIUtil.isEmpty(article.getTags())) {
        for (ItemTag tag : article.getTags()) {
          TextView tagView = (TextView) mInflater.inflate(
              R.layout.widget_tag_textview, mItemTags, false);
          tagView.setText(tag.getName());
          mItemTags.addView(tagView);
        }
      }
    }
  }
}
