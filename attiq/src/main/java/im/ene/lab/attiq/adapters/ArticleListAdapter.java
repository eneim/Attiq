package im.ene.lab.attiq.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.RequestCreator;
import com.wefika.flowlayout.FlowLayout;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.two.Article;
import im.ene.lab.attiq.data.two.ItemTag;
import im.ene.lab.attiq.data.two.User;
import im.ene.lab.attiq.util.TimeUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.RoundedTransformation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 1/10/16.
 */
public abstract class ArticleListAdapter extends ListAdapter<Article> {

  private final Object LOCK = new Object();

  private final ArrayList<Article> mItems;

  public ArticleListAdapter() {
    super();
    mItems = new ArrayList<>();
    setHasStableIds(true);
  }

  @Override public void clear() {
    synchronized (LOCK) {
      mItems.clear();
      notifyDataSetChanged();
    }
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final ViewHolder viewHolder = ViewHolder.createViewHolder(parent, viewType);
    viewHolder.setOnViewHolderClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
          mOnItemClickListener.onItemClick(
              ArticleListAdapter.this, viewHolder, view, position, getItemId(position));
        }
      }
    });

    return viewHolder;
  }

  // http://stackoverflow.com/a/10151694/1553254
  @Override public long getItemId(int position) {
    return new BigInteger(getItem(position).getId(), 16).longValue();
  }

  @Override public int getItemCount() {
    return mItems.size();
  }

  @Override public Article getItem(int position) {
    return mItems.get(position);
  }

  private Article getBottomItem() {
    return getItem(getItemCount() - 1);
  }

  @Override public void addItem(Article item) {
    synchronized (LOCK) {
      mItems.add(item);
      notifyItemInserted(getItemCount() - 1);
    }
  }

  @Override public void addItems(List<Article> items) {
    synchronized (LOCK) {
      int oldLen = getItemCount();
      mItems.addAll(items);
      notifyItemRangeInserted(oldLen, items.size());
    }
  }

  protected void cleanup(boolean shouldCleanup) {
    if (shouldCleanup) {
      synchronized (LOCK) {
        mItems.clear();
        notifyDataSetChanged();
      }
    }
  }

  public static abstract class OnArticleClickListener implements OnItemClickListener {

    @Override
    public void onItemClick(BaseAdapter adapter,
                            BaseAdapter.ViewHolder viewHolder,
                            View view, int adapterPos, long itemId) {
      final Article item;
      if (adapter instanceof BaseListAdapter) {
        item = (Article) ((BaseListAdapter) adapter).getItem(adapterPos);
      } else {
        item = null;
      }

      if (item != null && viewHolder instanceof ViewHolder) {
        if (view == ((ViewHolder) viewHolder).mItemUserImage) {
          onUserClick(item.getUser());
        } else if (view == ((ViewHolder) viewHolder).itemView) {
          onItemContentClick(item);
        }
      }
    }

    public abstract void onUserClick(User user);

    public abstract void onItemContentClick(Article item);
  }

  public static class ViewHolder extends BaseListAdapter.ViewHolder<Article> {

    static final int LAYOUT_RES = R.layout.post_item_view;

    public static ViewHolder createViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(ViewHolder.LAYOUT_RES, parent, false);
      return new ViewHolder(view);
    }

    private final LayoutInflater mInflater;
    private final Context mContext;
    // Views
    @Bind(R.id.item_user_icon) ImageView mItemUserImage;
    @Bind(R.id.item_title) TextView mItemTitle;
    @Bind(R.id.item_tags) FlowLayout mItemTags;
    @Bind(R.id.item_info) TextView mItemInfo;
    @Bind(R.id.item_posted_info) TextView mItemUserInfo;
    // Others
    @BindDimen(R.dimen.item_icon_size_half) int mIconCornerRadius;
    @BindDimen(R.dimen.dimen_unit) int mIconBorderWidth;
    @BindColor(R.color.colorAccent) int mIconBorderColor;

    public ViewHolder(View view) {
      super(view);
      mContext = itemView.getContext();
      mInflater = LayoutInflater.from(mContext);
      mItemInfo.setVisibility(View.GONE);
      mItemUserInfo.setClickable(true);
      mItemUserInfo.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override public void setOnViewHolderClickListener(View.OnClickListener listener) {
      super.setOnViewHolderClickListener(listener);
    }

    @Override public void bind(final Article item) {
      if (item.getUser() != null) {
        String userName = item.getUser().getId();
        mItemUserInfo.setText(Html.fromHtml(mContext.getString(R.string.item_user_info,
            userName, userName,
            TimeUtil.beautify(item.getCreatedAt())
        )));
        mItemUserInfo.setVisibility(View.VISIBLE);
      } else {
        mItemUserInfo.setVisibility(View.GONE);
      }

      mItemTitle.setText(Html.fromHtml(item.getTitle()));
      final RequestCreator requestCreator;
      if (!UIUtil.isEmpty(item.getUser().getProfileImageUrl())) {
        requestCreator = Attiq.picasso().load(item.getUser().getProfileImageUrl());
      } else {
        requestCreator = Attiq.picasso().load(R.drawable.blank_profile_icon_medium);
      }

      requestCreator
          .placeholder(R.drawable.blank_profile_icon_medium)
          .error(R.drawable.blank_profile_icon_medium)
          .fit().centerInside()
          .transform(new RoundedTransformation(
              mIconBorderWidth, mIconBorderColor, mIconCornerRadius))
          .into(mItemUserImage);

      mItemTags.removeAllViews();
      if (!UIUtil.isEmpty(item.getTags())) {
        for (ItemTag tag : item.getTags()) {
          final TextView tagName = (TextView) mInflater
              .inflate(R.layout.widget_tag_textview, mItemTags, false);
          tagName.setClickable(true);
          tagName.setMovementMethod(LinkMovementMethod.getInstance());
          tagName.setText(Html.fromHtml(mContext.getString(R.string.local_tag_url,
              tag.getName(), tag.getName())));

          TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tagName,
              ContextCompat.getDrawable(mContext, R.drawable.ic_lens_16dp), null, null, null);

          UIUtil.stripUnderlines(tagName);
          mItemTags.addView(tagName);
        }
      }
    }
  }
}
