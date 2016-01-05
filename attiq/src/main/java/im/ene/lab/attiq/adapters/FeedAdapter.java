package im.ene.lab.attiq.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
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
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.api.open.FeedItem;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.RoundedTransformation;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;

import java.util.List;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedAdapter extends AttiqListAdapter<FeedItem> {

  private final RealmResults<FeedItem> mItems;

  public FeedAdapter(RealmResults<FeedItem> items) {
    super();
    this.mItems = items;
    setHasStableIds(true);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(ViewHolder.LAYOUT_RES, parent, false);
    final ViewHolder viewHolder = new ViewHolder(view);

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

  public static class ViewHolder extends BaseListAdapter.ViewHolder<FeedItem> {

    static final int LAYOUT_RES = R.layout.feed_item_view;

    private final LayoutInflater mInflater;

    private final Realm mRealm;

    // Views
    @Bind(R.id.item_user_icon) ImageView mItemUserImage;
    @Bind(R.id.item_title) TextView mItemTitle;
    @Bind(R.id.item_tags) FlowLayout mItemTags;
    @Bind(R.id.item_info) TextView mItemInfo;
    @Bind(R.id.item_posted_info) TextView mItemUserInfo;

    // Others
    @BindDimen(R.dimen.tag_icon_size) int mTagIconSize;
    @BindDimen(R.dimen.tag_icon_size_half) int mTagIconSizeHalf;

    @BindDimen(R.dimen.item_icon_size_half) int mIconCornerRadius;
    @BindDimen(R.dimen.dimen_unit) int mIconBorderWidth;
    @BindColor(R.color.colorAccent) int mIconBorderColor;

    private final Context mContext;

    public ViewHolder(View view) {
      super(view);
      mRealm = Attiq.realm();
      mContext = itemView.getContext();
      mInflater = LayoutInflater.from(mContext);
      mItemUserInfo.setClickable(true);
      mItemUserInfo.setMovementMethod(LinkMovementMethod.getInstance());
      mItemTags.setVisibility(View.GONE);
    }

    @Override public void setOnViewHolderClickListener(View.OnClickListener listener) {
      mItemUserImage.setOnClickListener(listener);
      itemView.setOnClickListener(listener);
    }

    @Override public void bind(FeedItem item) {
      String itemInfo = item.getMentionedObjectCommentsCount() == 1 ?
          mContext.getString(R.string.item_info_one, item.getMentionedObjectStocksCount()) :
          mContext.getString(R.string.item_info_many, item.getMentionedObjectStocksCount(),
              item.getMentionedObjectCommentsCount());
      mItemInfo.setText(itemInfo);

      mItemUserInfo.setVisibility(View.GONE);

      mItemTitle.setText(Html.fromHtml(item.getMentionedObjectName()));
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

//      mItemTags.removeAllViews();
//      if (!UIUtil.isEmpty(item.getMentionedObjectTags())) {
//        for (PublicTag tag : item.getMentionedObjectTags()) {
//          final View tagView = mInflater.inflate(R.layout.widget_tag_view, mItemTags, false);
//          TextView tagName = (TextView) tagView.findViewById(R.id.tag_name);
//          final ImageView tagIcon = (ImageView) tagView.findViewById(R.id.tag_icon);
//          tagName.setText(tag.getName());
//
//          PublicTag storedTag = mRealm.where(PublicTag.class)
//              .equalTo("name", tag.getName()).findFirst();
//
//          if (storedTag != null) {
//            Attiq.picasso().load(storedTag.getIconUrl())
//                .resize(mTagIconSize, 0)
//                .transform(new RoundedTransformation(
//                    mIconBorderWidth, mIconBorderColor, mTagIconSizeHalf))
//                .into(tagIcon, new com.squareup.picasso.Callback() {
//                  @Override public void onSuccess() {
//                    tagIcon.setVisibility(View.VISIBLE);
//                    mItemTags.addView(tagView);
//                  }
//
//                  @Override public void onError() {
//                    tagIcon.setVisibility(View.GONE);
//                    mItemTags.addView(tagView);
//                  }
//                });
//          } else {
//            tagIcon.setVisibility(View.GONE);
//            mItemTags.addView(tagView);
//          }
//        }
//      }
    }
  }
}
