/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.attiq.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindDimen;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.model.zero.FeedItem;
import im.ene.lab.attiq.ui.widgets.RoundedTransformation;
import im.ene.lab.attiq.ui.widgets.TextViewTarget;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.TimeUtil;
import im.ene.lab.attiq.util.UIUtil;
import io.realm.RealmResults;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedListAdapter extends AttiqRealmListAdapter<FeedItem> {

  private static final int VIEW_TYPE_ITEM = 1 << 1;
  private static final int VIEW_TYPE_FOLLOW = 1 << 2;
  private final RealmResults<FeedItem> mItems;

  public FeedListAdapter(RealmResults<FeedItem> items) {
    super();
    this.mItems = items;
    setHasStableIds(true);
  }

  @Override public ViewHolder<FeedItem> onCreateViewHolder(ViewGroup parent, int viewType) {
    final ViewHolder<FeedItem> viewHolder;
    if (viewType == VIEW_TYPE_ITEM) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(FeedViewHolder.LAYOUT_RES, parent, false);
      viewHolder = new FeedViewHolder(view);
    } else {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(FollowingViewHolder.LAYOUT_RES, parent, false);
      viewHolder = new FollowingViewHolder(view);
    }

    viewHolder.setOnViewHolderClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION && clickListener != null) {
          clickListener.onItemClick(FeedListAdapter.this, viewHolder, view, position,
              getItemId(position));
        }
      }
    });

    return viewHolder;
  }

  @Override public FeedItem getItem(int position) {
    return mItems.get(position);
  }

  @Override public int getItemViewType(int position) {
    FeedItem item = getItem(position);
    if (FeedItem.TRACKABLE_TYPE_FOLLOW_TAG.equals(item.getTrackableType())
        || FeedItem.TRACKABLE_TYPE_FOLLOW_USER.equals(item.getTrackableType())) {
      return VIEW_TYPE_FOLLOW;
    }

    return VIEW_TYPE_ITEM;
  }

  @Override public long getItemId(int position) {
    return IOUtil.hashCode(getItem(position));
  }

  @Override public int getItemCount() {
    return mItems.size();
  }

  @Override
  public void loadItems(boolean isLoadingMore, int page, int pageLimit, @Nullable String query,
      final Callback<List<FeedItem>> callback) {
    final Long createdAt;
    if (getItemCount() == 0 || !isLoadingMore) {
      createdAt = null;
    } else {
      createdAt = getItem(getItemCount() - 1).getCreatedAtInUnixtime();
    }

    isLoading = true;
    ApiClient.feed(createdAt).enqueue(new Callback<List<FeedItem>>() {
      @Override
      public void onResponse(Call<List<FeedItem>> call, Response<List<FeedItem>> response) {
        isLoading = false;
        if (callback != null) {
          callback.onResponse(call, response);
        }
      }

      @Override public void onFailure(Call<List<FeedItem>> call, Throwable t) {
        isLoading = false;
        if (callback != null) {
          callback.onFailure(call, t);
        }
      }
    });
  }

  public static class FeedViewHolder extends ViewHolder<FeedItem> {

    private static final String TAG = "FeedViewHolder";

    static final int LAYOUT_RES = R.layout.feed_item_view;

    private final LayoutInflater mInflater;
    private final Context mContext;
    // Views
    @Bind(R.id.item_user_icon) ImageView mItemUserImage;
    @Bind(R.id.item_title) TextView mItemTitle;
    @Bind(R.id.item_tags) FlexboxLayout mItemTags;
    @Bind(R.id.item_info) TextView mItemInfo;
    @Bind(R.id.item_posted_info) TextView mItemUserInfo;
    @Bind(R.id.feed_item_identity) LinearLayout mItemIdentity;
    // Others
    @BindDimen(R.dimen.tag_icon_size) int mTagIconSize;
    @BindDimen(R.dimen.tag_icon_size_half) int mTagIconSizeHalf;
    @BindDimen(R.dimen.item_icon_size_half) int mIconCornerRadius;
    @BindDimen(R.dimen.dimen_unit) int mIconBorderWidth;
    int mIconBorderColor;

    public FeedViewHolder(View view) {
      super(view);
      mContext = itemView.getContext();
      mInflater = LayoutInflater.from(mContext);
      mItemUserInfo.setClickable(true);
      mItemUserInfo.setMovementMethod(LinkMovementMethod.getInstance());
      mItemTags.setVisibility(View.GONE);
      mItemUserInfo.setVisibility(View.GONE);

      TypedValue typedValue = new TypedValue();
      itemView.getContext()
          .getTheme()
          .resolveAttribute(android.R.attr.colorAccent, typedValue, true);
      mIconBorderColor = typedValue.resourceId;
    }

    void setupItemClick(FeedViewHolder vh, View view, FeedItem item,
        OnFeedItemClickListener listener) {
      if (listener == null) {
        return;
      }

      if (view == vh.itemView) {
        listener.onItemContentClick(item);
      } else if (view == vh.mItemUserImage) {
        listener.onMentionedUserClick(item);
      }
    }

    @Override public void setOnViewHolderClickListener(View.OnClickListener listener) {
      mItemUserImage.setOnClickListener(listener);
      itemView.setOnClickListener(listener);
    }

    @Override public void bind(FeedItem item) {
      String itemInfo =
          Integer.valueOf(1).equals(item.getMentionedObjectCommentsCount()) ? mContext.getString(
              R.string.item_info_one, item.getMentionedObjectStocksCount())
              : mContext.getString(R.string.item_info_many, item.getMentionedObjectStocksCount(),
                  item.getMentionedObjectCommentsCount());
      mItemInfo.setText(itemInfo);

      mItemTitle.setText(item.getMentionedObjectName());
      final RequestCreator requestCreator;
      if (!UIUtil.isEmpty(item.getMentionedObjectImageUrl())) {
        requestCreator = Attiq.picasso().load(item.getMentionedObjectImageUrl());
      } else {
        requestCreator = Attiq.picasso().load(R.drawable.blank_profile_icon_medium);
      }

      requestCreator.placeholder(R.drawable.blank_profile_icon_medium)
          .error(R.drawable.blank_profile_icon_medium)
          .fit()
          .centerInside()
          .transform(
              new RoundedTransformation(mIconBorderWidth, mIconBorderColor, mIconCornerRadius))
          .into(mItemUserImage);

      mItemIdentity.setVisibility(View.VISIBLE);
      mItemIdentity.removeAllViews();

      if (FeedItem.TRACKABLE_TYPE_TAG.equals(item.getTrackableType())) {
        final View tagView = mInflater.inflate(R.layout.layout_post_tag, mItemTags, false);
        final TextView tagName = (TextView) tagView.findViewById(R.id.post_tag_name);
        mItemIdentity.addView(tagView);

        tagName.setText(item.getFollowableName());
        ImageView tagIcon = (ImageView) tagView.findViewById(R.id.post_tag_icon);
        Glide.with(mContext)
            .load(item.getFollowableImageUrl())
            .apply(requestOptions.clone()
                .placeholder(R.drawable.ic_local_offer_black_24dp)
                .error(R.drawable.ic_local_offer_black_24dp))
            .into(tagIcon);
        final TextView infoText =
            (TextView) mInflater.inflate(R.layout.single_line_text_tiny, mItemIdentity, false);
        infoText.setText(R.string.tag_new_post);

        mItemIdentity.addView(infoText);
      } else if (FeedItem.TRACKABLE_TYPE_STOCK.equals(item.getTrackableType())) {
        TextView infoText =
            (TextView) mInflater.inflate(R.layout.single_line_text_tiny, mItemIdentity, false);
        infoText.setClickable(true);
        infoText.setMovementMethod(LinkMovementMethod.getInstance());
        infoText.setText(Html.fromHtml(
            mContext.getString(R.string.user_stocked, item.getFollowableName(),
                item.getFollowableName())));

        infoText.setId(R.id.feed_view_id_info);
        UIUtil.stripUnderlines(infoText, null, false);
        mItemIdentity.addView(infoText);
      } else if (FeedItem.TRACKABLE_TYPE_COMMENT.equals(item.getTrackableType())) {
        TextView infoText =
            (TextView) mInflater.inflate(R.layout.single_line_text_tiny, mItemIdentity, false);
        infoText.setClickable(true);
        infoText.setMovementMethod(LinkMovementMethod.getInstance());
        infoText.setText(Html.fromHtml(
            mContext.getString(R.string.user_commented, item.getFollowableName(),
                item.getFollowableName())));

        infoText.setId(R.id.feed_view_id_info);
        UIUtil.stripUnderlines(infoText, null, false);
        mItemIdentity.addView(infoText);
      } else if (FeedItem.TRACKABLE_TYPE_PUBLIC.equals(item.getTrackableType())) {
        TextView infoText =
            (TextView) mInflater.inflate(R.layout.single_line_text_tiny, mItemIdentity, false);
        infoText.setClickable(true);
        infoText.setMovementMethod(LinkMovementMethod.getInstance());

        String userName = item.getFollowableName();
        infoText.setText(Html.fromHtml(mContext.getString(R.string.item_user_info_plain, userName,
            TimeUtil.beautify(item.getCreatedAtInUnixtime()))));

        infoText.setId(R.id.feed_view_id_info);
        UIUtil.stripUnderlines(infoText, null, false);
        mItemIdentity.addView(infoText);
      } else {
        mItemIdentity.setVisibility(View.GONE);
      }
    }
  }

  public static class FollowingViewHolder extends ViewHolder<FeedItem> {

    static final int LAYOUT_RES = R.layout.feed_item_simple_view;
    private final LayoutInflater mInflater;
    @Bind(R.id.item_info) TextView mItemInfo;
    @Bind(R.id.container) ViewGroup mContainer;
    // Others
    @BindDimen(R.dimen.item_icon_size_small) int mUserIconSize;
    @BindDimen(R.dimen.item_icon_size_small_half) int mUserIconSizeHalf;
    @BindDimen(R.dimen.dimen_unit) int mIconBorderWidth;
    int mIconBorderColor;
    @BindDimen(R.dimen.tag_icon_size) int mTagIconSize;
    @BindDimen(R.dimen.tag_icon_size_half) int mTagIconSizeHalf;

    public FollowingViewHolder(@NonNull View itemView) {
      super(itemView);
      mInflater = LayoutInflater.from(itemView.getContext());
      mItemInfo.setClickable(true);
      mItemInfo.setMovementMethod(LinkMovementMethod.getInstance());

      TypedValue typedValue = new TypedValue();
      itemView.getContext()
          .getTheme()
          .resolveAttribute(android.R.attr.colorAccent, typedValue, true);
      mIconBorderColor = typedValue.resourceId;
    }

    @Override public void bind(FeedItem item) {
      mItemInfo.setText(Html.fromHtml(itemView.getContext()
          .getString(R.string.user_follow, item.getFollowableName(), item.getFollowableName())));
      UIUtil.stripUnderlines(mItemInfo, null, false);

      TextView itemName;
      if ((itemName = (TextView) itemView.findViewById(R.id.feed_view_id_mentioned_item)) != null) {
        mContainer.removeView(itemName);
      }

      // User follows new tag
      if (FeedItem.TRACKABLE_TYPE_FOLLOW_TAG.equals(item.getTrackableType())) {
        itemName = (TextView) mInflater.inflate(R.layout.widget_tag_textview, mContainer, false);
        itemName.setClickable(true);
        itemName.setMovementMethod(LinkMovementMethod.getInstance());

        itemName.setText(Html.fromHtml(itemView.getContext()
            .getString(R.string.local_tag_url, item.getMentionedObjectName(),
                item.getMentionedObjectName())));

        Attiq.picasso()
            .load(item.getMentionedObjectImageUrl())
            .resize(0, mTagIconSize)
            .transform(
                new RoundedTransformation(mIconBorderWidth, mIconBorderColor, mTagIconSizeHalf))
            .into(new TextViewTarget(itemName) {
              @Override public void onBitmapLoaded(TextView textView, Bitmap bitmap,
                  Picasso.LoadedFrom from) {
                RoundedBitmapDrawable drawable =
                    RoundedBitmapDrawableFactory.create(itemView.getResources(), bitmap);
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, drawable,
                    null, null, null);
              }
            });
        UIUtil.stripUnderlines(itemName);
      } else {
        itemName = (TextView) mInflater.inflate(R.layout.widget_user_textview, mContainer, false);
        itemName.setClickable(true);
        itemName.setMovementMethod(LinkMovementMethod.getInstance());

        itemName.setText(Html.fromHtml(itemView.getContext()
            .getString(R.string.user_name, item.getMentionedObjectName(),
                item.getMentionedObjectName())));
        UIUtil.stripUnderlines(itemName, null, false);

        Attiq.picasso()
            .load(item.getMentionedObjectImageUrl())
            .resize(mUserIconSize, 0)
            .transform(
                new RoundedTransformation(mIconBorderWidth, mIconBorderColor, mUserIconSizeHalf))
            .into(new TextViewTarget(itemName) {
              @Override public void onBitmapLoaded(TextView textView, Bitmap bitmap,
                  Picasso.LoadedFrom from) {
                RoundedBitmapDrawable drawable =
                    RoundedBitmapDrawableFactory.create(itemView.getResources(), bitmap);
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, drawable,
                    null, null, null);
              }
            });
      }

      itemName.setId(R.id.feed_view_id_mentioned_item);
      mContainer.addView(itemName);
    }
  }

  public static abstract class OnFeedItemClickListener implements OnItemClickListener {

    public abstract void onMentionedUserClick(FeedItem host);

    public abstract void onItemContentClick(FeedItem item);

    @Override
    public void onItemClick(BaseAdapter adapter, BaseAdapter.ViewHolder viewHolder, View view,
        int adapterPosition, long itemId) {
      final FeedItem item;
      if (adapter instanceof BaseListAdapter) {
        item = (FeedItem) ((BaseListAdapter) adapter).getItem(adapterPosition);
      } else {
        item = null;
      }

      if (item == null) {
        return;
      }

      if (viewHolder instanceof FeedViewHolder) {
        ((FeedViewHolder) viewHolder).setupItemClick((FeedViewHolder) viewHolder, view, item, this);
      }
    }
  }
}
