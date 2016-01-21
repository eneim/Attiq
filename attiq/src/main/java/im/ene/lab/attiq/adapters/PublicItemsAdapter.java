package im.ene.lab.attiq.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.wefika.flowlayout.FlowLayout;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.one.PublicTag;
import im.ene.lab.attiq.data.one.PublicUser;
import im.ene.lab.attiq.data.zero.Post;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.TextViewTarget;
import im.ene.lab.attiq.util.TimeUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.RoundedTransformation;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * Created by eneim on 12/14/15.
 */
public class PublicItemsAdapter extends RealmListAdapter<Post> {

  private final RealmResults<Post> mItems;

  public PublicItemsAdapter(RealmResults<Post> items) {
    super();
    mItems = items;
    setHasStableIds(true);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(ViewHolder.LAYOUT_RES, parent, false);
    final ViewHolder viewHolder = new ViewHolder(view);
    viewHolder.setOnViewHolderClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        int position = viewHolder.getAdapterPosition();
        Log.d(TAG, "onClick() called with: " + "view = [" + view + "]" + " position = " +
            "[" + position + "]");
        if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
          mOnItemClickListener.onItemClick(PublicItemsAdapter.this, viewHolder, view, position,
              getItemId(position));
        }
      }
    });

    return viewHolder;
  }

  @Override public long getItemId(int position) {
    return getItem(position).getId();
  }

  @Override public int getItemCount() {
    if (mItems == null || !mItems.isValid()) {
      return 0;
    }
    return mItems.size();
  }

  @Override public Post getItem(int position) {
    return mItems.get(position);
  }

  private Post getBottomItem() {
    return getItem(getItemCount() - 1);
  }

  private static final String TAG = "PublicItemsAdapter";

  @Override
  public void loadItems(final boolean isLoadingMore, int page, int pageLimit,
                        @Nullable String query, final Callback<List<Post>> callback) {
    Log.d(TAG, "loadItems() called with: " + "isLoadingMore = [" + isLoadingMore + "], page = ["
        + page + "], pageLimit = [" + pageLimit + "]");

    final Call<List<Post>> data;
    if (UIUtil.isEmpty(PrefUtil.getCurrentToken())) {
      data = ApiClient.openStream(page, pageLimit);
    } else {
      Long id = null;
      if (isLoadingMore) {
        id = getBottomItem().getId();
      }

      data = ApiClient.publicStream(id);
    }

    data.enqueue(new Callback<List<Post>>() {
      @Override public void onResponse(Response<List<Post>> response) {
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

  private void cleanup(boolean shouldCleanup) {
    if (shouldCleanup) {
      Realm realm = Attiq.realm();
      realm.beginTransaction();
      realm.clear(Post.class);
      realm.commitTransaction();
      realm.close();
    }
  }

  public static abstract class OnPublicItemClickListener
      implements BaseAdapter.OnItemClickListener {

    public abstract void onUserClick(PublicUser user);

    public abstract void onItemContentClick(Post item);

    @Override
    public void onItemClick(BaseAdapter adapter,
                            BaseAdapter.ViewHolder viewHolder,
                            View view, int adapterPos, long itemId) {
      final Post item;
      if (adapter instanceof BaseListAdapter) {
        item = (Post) ((BaseListAdapter) adapter).getItem(adapterPos);
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
  }

  public static class ViewHolder extends BaseListAdapter.ViewHolder<Post> {

    static final int LAYOUT_RES = R.layout.post_item_view;

    private final LayoutInflater mInflater;

    // Views
    @Bind(R.id.item_user_icon) ImageView mItemUserImage;
    @Bind(R.id.item_title) TextView mItemTitle;
    @Bind(R.id.item_tags) FlowLayout mItemTags;
    @Bind(R.id.item_info) TextView mItemInfo;
    @Bind(R.id.item_posted_info) TextView mItemUserInfo;

    // Others
    @BindDimen(R.dimen.item_icon_size_half) int mIconCornerRadius;
    @BindDimen(R.dimen.image_border_small) int mIconBorderWidth;
    @BindColor(R.color.colorAccent) int mIconBorderColor;

    @BindDimen(R.dimen.tag_icon_size) int mTagIconSize;
    @BindDimen(R.dimen.tag_icon_size_half) int mTagIconSizeHalf;

    private final Context mContext;

    public ViewHolder(View view) {
      super(view);
      mContext = itemView.getContext();
      mInflater = LayoutInflater.from(mContext);
      mItemUserInfo.setClickable(true);
      mItemUserInfo.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override public void setOnViewHolderClickListener(View.OnClickListener listener) {
      mItemUserImage.setOnClickListener(listener);
      itemView.setOnClickListener(listener);
    }

    @Override public void bind(Post item) {
      String itemInfo = item.getCommentCount() == 1 ?
          mContext.getString(R.string.item_info_one, item.getStockCount()) :
          mContext.getString(R.string.item_info_many, item.getStockCount(), item.getCommentCount());
      mItemInfo.setText(itemInfo);

      if (item.getUser() != null) {
        String userName = item.getUser().getUrlName();
        mItemUserInfo.setText(Html.fromHtml(mContext.getString(R.string.item_user_info,
            userName, userName,
            TimeUtil.beautify(item.getCreatedAtAsSeconds())
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
      final Resources res = mItemTags.getResources();
      if (!UIUtil.isEmpty(item.getTags())) {
        for (PublicTag tag : item.getTags()) {
          TextView tagName = (TextView) mInflater
              .inflate(R.layout.widget_tag_textview, mItemTags, false);

          tagName.setClickable(true);
          tagName.setMovementMethod(LinkMovementMethod.getInstance());
          tagName.setText(Html.fromHtml(mContext.getString(R.string.local_tag_url,
              tag.getName(), tag.getName())));

          Attiq.picasso().load(tag.getIconUrl())
              .placeholder(R.drawable.ic_dnd_forwardslash_16dp)
              .error(R.drawable.ic_dnd_forwardslash_16dp)
              .resize(0, mTagIconSize)
              .transform(new RoundedTransformation(
                  mIconBorderWidth, mIconBorderColor, mTagIconSizeHalf))
              .into(new TextViewTarget(tagName) {
                @Override
                public void onBitmapLoaded(TextView textView,
                                           Bitmap bitmap, Picasso.LoadedFrom from) {
                  RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(res, bitmap);
                  TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(textView,
                      drawable, null, null, null);
                }
              });

          UIUtil.stripUnderlines(tagName);
          mItemTags.addView(tagName);
        }
      }
    }
  }
}
