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
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindDimen;
import com.squareup.picasso.RequestCreator;
import com.wefika.flowlayout.FlowLayout;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.model.local.StockArticle;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.two.ItemTag;
import im.ene.lab.attiq.data.model.two.User;
import im.ene.lab.attiq.ui.widgets.RoundedTransformation;
import im.ene.lab.attiq.util.TimeUtil;
import im.ene.lab.attiq.util.UIUtil;
import io.realm.Realm;
import io.realm.RealmResults;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eneim on 1/10/16.
 */
public class UserStockArticlesAdapter extends RealmListAdapter<StockArticle> {

  private final String mUserId;

  private final RealmResults<StockArticle> mItems;

  public UserStockArticlesAdapter(String userId, RealmResults<StockArticle> items) {
    super();
    this.mUserId = userId;
    this.mItems = items;
    setHasStableIds(true);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final ViewHolder viewHolder = ViewHolder.createViewHolder(parent, viewType);
    viewHolder.setOnViewHolderClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
          mOnItemClickListener.onItemClick(
              UserStockArticlesAdapter.this, viewHolder, view, position, getItemId(position));
        }
      }
    });

    return viewHolder;
  }

  // http://stackoverflow.com/a/10151694/1553254
  @Override public long getItemId(int position) {
    return new BigInteger(getItem(position).getArticleId(), 16).longValue();
  }

  @Override public int getItemCount() {
    return mItems.size();
  }

  @Override public StockArticle getItem(int position) {
    return mItems.get(position);
  }

  private void cleanup(boolean shouldCleanup) {
    if (shouldCleanup) {
      Realm realm = Attiq.realm();
      realm.beginTransaction();
      realm.clear(StockArticle.class);
      realm.commitTransaction();
      realm.close();
    }
  }

  private static final String TAG = "StockedArticlesAdapter";

  @Override
  public void loadItems(final boolean isLoadingMore, int page, int pageLimit,
                        @Nullable String query, final Callback<List<StockArticle>> callback) {
    // do nothing
    isLoading = true;
    ApiClient.userStockedItems(mUserId, page, pageLimit).enqueue(new Callback<List<Article>>() {
      @Override public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
        isLoading = false;
        // cleanup(!isLoadingMore);
        if (callback != null) {
          final List<Article> items = response.body();
          List<StockArticle> articles = new ArrayList<>();
          if (!UIUtil.isEmpty(items)) {
            for (Article item : items) {
              StockArticle article = new StockArticle();
              article.setArticleId(item.getId());
              article.setCreatedAt(TimeUtil.epochV2(item.getCreatedAt()));
              article.setUserId(mUserId);
              article.setArticle(item);
              articles.add(article);
            }
          }
          callback.onResponse(null, Response.success(articles));
        }
      }

      @Override public void onFailure(Call<List<Article>> call, Throwable throwable) {
        isLoading = false;
        // cleanup(!isLoadingMore);
        if (callback != null) {
          callback.onFailure(null, throwable);
        }
      }
    });
  }

  public static abstract class OnArticleClickListener implements OnItemClickListener {

    @Override
    public void onItemClick(BaseAdapter adapter,
                            BaseAdapter.ViewHolder viewHolder,
                            View view, int adapterPos, long itemId) {
      final StockArticle item;
      if (adapter instanceof BaseListAdapter) {
        item = (StockArticle) ((BaseListAdapter) adapter).getItem(adapterPos);
      } else {
        item = null;
      }

      if (item != null && viewHolder instanceof ViewHolder) {
        if (view == ((ViewHolder) viewHolder).mItemUserImage) {
          onUserClick(item.getArticle().getUser());
        } else if (view == ((ViewHolder) viewHolder).itemView) {
          onItemContentClick(item.getArticle());
        }
      }
    }

    public abstract void onUserClick(User user);

    public abstract void onItemContentClick(Article item);
  }

  public static class ViewHolder extends BaseListAdapter.ViewHolder<StockArticle> {

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
    @BindDimen(R.dimen.image_border_small) int mIconBorderWidth;
    int mIconBorderColor;

    public ViewHolder(View view) {
      super(view);
      mContext = itemView.getContext();
      mInflater = LayoutInflater.from(mContext);
      mItemInfo.setVisibility(View.GONE);
      mItemUserInfo.setClickable(true);
      mItemUserInfo.setMovementMethod(LinkMovementMethod.getInstance());

      TypedValue typedValue = new TypedValue();
      mContext.getTheme()
          .resolveAttribute(android.R.attr.colorAccent, typedValue, true);
      mIconBorderColor = typedValue.resourceId;
    }

    @Override public void setOnViewHolderClickListener(View.OnClickListener listener) {
      super.setOnViewHolderClickListener(listener);
      mItemUserImage.setOnClickListener(listener);
    }

    @Override public void bind(final StockArticle historyItem) {
      Article item = historyItem.getArticle();
      if (item == null) {
        return;
      }

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
