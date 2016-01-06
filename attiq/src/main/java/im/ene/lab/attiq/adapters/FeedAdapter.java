package im.ene.lab.attiq.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.RequestCreator;
import com.wefika.flowlayout.FlowLayout;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.api.open.FeedItem;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.RoundedTransformation;
import io.realm.RealmResults;
import retrofit2.Callback;

import java.util.List;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedAdapter extends AttiqListAdapter<FeedItem> {

  private final RealmResults<FeedItem> mItems;

  private static final int VIEW_TYPE_ITEM = 1 << 1;

  private static final int VIEW_TYPE_FOLLOW = 1 << 2;

  public FeedAdapter(RealmResults<FeedItem> items) {
    super();
    this.mItems = items;
    setHasStableIds(true);
  }

  @Override
  public ViewHolder<FeedItem> onCreateViewHolder(ViewGroup parent, int viewType) {
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
        if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
          mOnItemClickListener.onItemClick(FeedAdapter.this, viewHolder, view, position,
              getItemId(position));
        }
      }
    });

    return viewHolder;
  }

  @Override public int getItemCount() {
    if (mItems == null || !mItems.isValid()) {
      return 0;
    }
    return mItems.size();
  }

  @Override public FeedItem getItem(int position) {
    return mItems.get(position);
  }

  @Override public long getItemId(int position) {
    return getItem(position).getCreatedAtInUnixtime();
  }

  @Override public int getItemViewType(int position) {
    FeedItem item = getItem(position);
    if (FeedItem.TRACKABLE_TYPE_FOLLOW_TAG.equals(item.getTrackableType()) ||
        FeedItem.TRACKABLE_TYPE_FOLLOW_USER.equals(item.getTrackableType())) {
      return VIEW_TYPE_FOLLOW;
    }

    return VIEW_TYPE_ITEM;
  }

  @Override
  public void loadItems(boolean isLoadingMore, int page, int pageLimit,
                        @Nullable String query, Callback<List<FeedItem>> callback) {
    final Long createdAt;
    if (getItemCount() == 0) {
      createdAt = null;
    } else {
      createdAt = getItem(getItemCount() - 1).getCreatedAtInUnixtime();
    }

    ApiClient.feed(createdAt).enqueue(callback);
  }

  public static class FeedViewHolder extends BaseListAdapter.ViewHolder<FeedItem> {

    static final int LAYOUT_RES = R.layout.feed_item_view;

    private final LayoutInflater mInflater;

    // Views
    @Bind(R.id.item_user_icon) ImageView mItemUserImage;
    @Bind(R.id.item_title) TextView mItemTitle;
    @Bind(R.id.item_tags) FlowLayout mItemTags;
    @Bind(R.id.item_info) TextView mItemInfo;
    @Bind(R.id.item_posted_info) TextView mItemUserInfo;
    @Bind(R.id.feed_item_identity) LinearLayout mItemIdentity;

    // Others
    @BindDimen(R.dimen.tag_icon_size) int mTagIconSize;
    @BindDimen(R.dimen.tag_icon_size_half) int mTagIconSizeHalf;

    @BindDimen(R.dimen.item_icon_size_half) int mIconCornerRadius;
    @BindDimen(R.dimen.dimen_unit) int mIconBorderWidth;
    @BindColor(R.color.colorAccent) int mIconBorderColor;

    // TODO take care of memory leak
    private View.OnClickListener mListener;

    private final Context mContext;

    public FeedViewHolder(View view) {
      super(view);
      mContext = itemView.getContext();
      mInflater = LayoutInflater.from(mContext);
      mItemUserInfo.setClickable(true);
      mItemUserInfo.setMovementMethod(LinkMovementMethod.getInstance());
      mItemTags.setVisibility(View.GONE);
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
      } else if (view.getId() == R.id.feed_view_id_tag) {
        listener.onMentionedTagClick(item);
      }
    }

    @Override public void setOnViewHolderClickListener(View.OnClickListener listener) {
      mListener = listener;
      mItemUserImage.setOnClickListener(listener);
      itemView.setOnClickListener(listener);
    }

    @Override public void bind(FeedItem item) {
      String itemInfo = Integer.valueOf(1).equals(item.getMentionedObjectCommentsCount()) ?
          mContext.getString(R.string.item_info_one, item.getMentionedObjectStocksCount()) :
          mContext.getString(R.string.item_info_many, item.getMentionedObjectStocksCount(),
              item.getMentionedObjectCommentsCount());
      mItemInfo.setText(itemInfo);

      mItemUserInfo.setVisibility(View.GONE);

      mItemTitle.setText(item.getMentionedObjectName());
      final RequestCreator requestCreator;
      if (!UIUtil.isEmpty(item.getMentionedObjectImageUrl())) {
        requestCreator = Attiq.picasso().load(item.getMentionedObjectImageUrl());
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

      mItemIdentity.setVisibility(View.VISIBLE);
      mItemIdentity.removeAllViews();
      if (FeedItem.TRACKABLE_TYPE_TAG.equals(item.getTrackableType())) {
        final View tagView = mInflater.inflate(R.layout.widget_tag_view, mItemIdentity, false);
        final TextView tagName = ButterKnife.findById(tagView, R.id.tag_name);
        final ImageView tagIcon = ButterKnife.findById(tagView, R.id.tag_icon);

        tagView.setId(R.id.feed_view_id_tag);
        tagView.setOnClickListener(mListener);
        mItemIdentity.addView(tagView);

        tagName.setText(item.getFollowableName());

        Attiq.picasso().load(item.getFollowableImageUrl())
            .resize(mTagIconSize, 0)
            .transform(new RoundedTransformation(
                mIconBorderWidth, mIconBorderColor, mTagIconSizeHalf))
            .into(tagIcon, new com.squareup.picasso.Callback() {
              @Override public void onSuccess() {
                tagIcon.setVisibility(View.VISIBLE);
              }

              @Override public void onError() {
                tagIcon.setVisibility(View.GONE);
              }
            });

        final TextView infoText = (TextView) mInflater.inflate(R.layout.single_line_text_tiny,
            mItemIdentity, false);
        infoText.setText(R.string.tag_new_post);
        mItemIdentity.addView(infoText);
      } else if (FeedItem.TRACKABLE_TYPE_STOCK.equals(item.getTrackableType())) {
        TextView infoText = (TextView) mInflater.inflate(R.layout.single_line_text_tiny,
            mItemIdentity, false);
        infoText.setClickable(true);
        infoText.setMovementMethod(LinkMovementMethod.getInstance());
        infoText.setText(Html.fromHtml(mContext.getString(R.string.user_stocked,
            item.getFollowableName(), item.getFollowableName())));

        infoText.setId(R.id.feed_view_id_info);
        mItemIdentity.addView(infoText);
      } else if (FeedItem.TRACKABLE_TYPE_COMMENT.equals(item.getTrackableType())) {
        TextView infoText = (TextView) mInflater.inflate(R.layout.single_line_text_tiny,
            mItemIdentity, false);
        infoText.setClickable(true);
        infoText.setMovementMethod(LinkMovementMethod.getInstance());
        infoText.setText(Html.fromHtml(mContext.getString(R.string.user_commented,
            item.getFollowableName(), item.getFollowableName())));
        infoText.setId(R.id.feed_view_id_info);
        mItemIdentity.addView(infoText);
      } else {
        mItemIdentity.setVisibility(View.GONE);
      }
    }
  }

  public static class FollowingViewHolder extends ViewHolder<FeedItem> {

    static final int LAYOUT_RES = R.layout.feed_item_simple_view;

    @Bind(R.id.item_info) TextView mItemInfo;
    // @Bind(R.id.item_tag) View mItemTag;

    // Others
    @BindDimen(R.dimen.item_icon_size_small) int mTagIconSize;
    @BindDimen(R.dimen.item_icon_size_small_half) int mTagIconSizeHalf;

    @BindDimen(R.dimen.item_icon_size_half) int mIconCornerRadius;
    @BindDimen(R.dimen.dimen_unit) int mIconBorderWidth;
    @BindColor(R.color.colorAccent) int mIconBorderColor;

    private final LayoutInflater mInflater;

    private View.OnClickListener mListener;

    public FollowingViewHolder(@NonNull View itemView) {
      super(itemView);
      mInflater = LayoutInflater.from(itemView.getContext());
      mItemInfo.setClickable(true);
      mItemInfo.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override public void setOnViewHolderClickListener(View.OnClickListener listener) {
      mListener = listener;
      itemView.setOnClickListener(listener);
    }

    void setupItemClick(FollowingViewHolder vh, View view, FeedItem item,
                        OnFeedItemClickListener listener) {
      if (listener == null) {
        return;
      }

      if (view.getId() == R.id.feed_view_id_mentioned_item) {
        if (FeedItem.TRACKABLE_TYPE_FOLLOW_TAG.equals(item.getTrackableType())) {
          listener.onMentionedTagClick(item);
        } else if (FeedItem.TRACKABLE_TYPE_FOLLOW_USER.equals(item.getTrackableType())) {
          listener.onMentionedUserClick(item);
        }
      }
    }

    @Override public void bind(FeedItem item) {
      mItemInfo.setText(
          Html.fromHtml(itemView.getContext().getString(R.string.user_follow,
              item.getFollowableName(), item.getFollowableName()))
      );

      LinearLayoutCompat container = (LinearLayoutCompat) itemView;
      View mentionedItem;
      if ((mentionedItem = itemView.findViewById(R.id.feed_view_id_mentioned_item)) != null) {
        container.removeView(mentionedItem);
      }

      if (FeedItem.TRACKABLE_TYPE_FOLLOW_TAG.equals(item.getTrackableType())) {
        mentionedItem = mInflater.inflate(R.layout.widget_tag_view, container, false);

        final TextView tagName = ButterKnife.findById(mentionedItem, R.id.tag_name);
        final ImageView tagIcon = ButterKnife.findById(mentionedItem, R.id.tag_icon);

        tagName.setText(item.getMentionedObjectName());

        Attiq.picasso().load(item.getMentionedObjectImageUrl())
            .resize(mTagIconSize, 0)
            .transform(new RoundedTransformation(
                mIconBorderWidth, mIconBorderColor, mTagIconSizeHalf))
            .into(tagIcon, new com.squareup.picasso.Callback() {
              @Override public void onSuccess() {
                tagIcon.setVisibility(View.VISIBLE);
              }

              @Override public void onError() {
                tagIcon.setVisibility(View.GONE);
              }
            });

      } else {
        mentionedItem = mInflater.inflate(R.layout.widget_user_view, container, false);

        final TextView userName = ButterKnife.findById(mentionedItem, R.id.user_name);
        userName.setClickable(true);
        userName.setMovementMethod(LinkMovementMethod.getInstance());
        final ImageView userImage = ButterKnife.findById(mentionedItem, R.id.user_image);

        userName.setText(Html.fromHtml(itemView.getContext().getString(R.string.user_name,
            item.getMentionedObjectName(), item.getMentionedObjectName())));

        Attiq.picasso().load(item.getMentionedObjectImageUrl())
            .resize(mTagIconSize, 0)
            .transform(new RoundedTransformation(
                mIconBorderWidth, mIconBorderColor, mTagIconSizeHalf))
            .into(userImage, new com.squareup.picasso.Callback() {
              @Override public void onSuccess() {
                userImage.setVisibility(View.VISIBLE);
              }

              @Override public void onError() {
                userImage.setVisibility(View.GONE);
              }
            });
      }

      mentionedItem.setId(R.id.feed_view_id_mentioned_item);
      mentionedItem.setOnClickListener(mListener);
      container.addView(mentionedItem);
    }
  }

  public static abstract class OnFeedItemClickListener
      implements BaseAdapter.OnItemClickListener {

    public abstract void onMentionedUserClick(FeedItem host);

    public abstract void onItemContentClick(FeedItem item);

    public abstract void onMentionedTagClick(FeedItem host);

    @Override
    public void onItemClick(BaseAdapter adapter, BaseAdapter.ViewHolder viewHolder,
                            View view, int adapterPosition, long itemId) {
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
        ((FeedViewHolder) viewHolder)
            .setupItemClick((FeedViewHolder) viewHolder, view, item, this);
      } else if (viewHolder instanceof FollowingViewHolder) {
        ((FollowingViewHolder) viewHolder)
            .setupItemClick((FollowingViewHolder) viewHolder, view, item, this);
      }
    }
  }
}
