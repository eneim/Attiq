package im.ene.lab.attiq.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.picasso.RequestCreator;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.two.ItemTag;
import im.ene.lab.attiq.ui.widgets.RoundedTransformation;
import im.ene.lab.attiq.util.TimeUtil;
import im.ene.lab.attiq.util.UIUtil;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                        final Callback<List<Article>> callback) {
    isLoading = true;
    ApiClient.tagItems(mTagId, page, pageLimit).enqueue(new Callback<List<Article>>() {
      @Override public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
        isLoading = false;
        if (callback != null) {
          callback.onResponse(call, response);
        }
      }

      @Override public void onFailure(Call<List<Article>> call, Throwable t) {
        isLoading = false;
        if (callback != null) {
          callback.onFailure(call, t);
        }
      }
    });
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final ViewHolder viewHolder =
        new ViewHolder(super.onCreateViewHolder(parent, viewType).itemView, mTagId);
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

    private final Context mContext;
    private final Spannable mIgnoredUrl;

    public ViewHolder(View view, String tagId) {
      super(view);
      mContext = view.getContext();
      mIgnoredUrl = Spannable.Factory.getInstance().newSpannable(
          Html.fromHtml(mContext.getString(R.string.local_tag_url, tagId, tagId))
      );
    }

    @Override public void setOnViewHolderClickListener(View.OnClickListener listener) {
      super.setOnViewHolderClickListener(listener);
      mItemUserImage.setOnClickListener(listener);
    }

    @Override public void bind(Article item) {
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
          final TextView tagName = (TextView) LayoutInflater.from(mItemTags.getContext())
              .inflate(R.layout.widget_tag_textview, mItemTags, false);
          tagName.setClickable(true);
          tagName.setMovementMethod(LinkMovementMethod.getInstance());
          tagName.setText(Html.fromHtml(mContext.getString(R.string.local_tag_url,
              tag.getName(), tag.getName())));

          TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tagName,
              ContextCompat.getDrawable(mContext, R.drawable.ic_lens_16dp), null, null, null);

          UIUtil.stripUnderlines(tagName, mIgnoredUrl);
          mItemTags.addView(tagName);
        }
      }
    }
  }
}
