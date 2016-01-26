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

package im.ene.lab.attiq.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.common.MoPub;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;

import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.model.one.PublicUser;
import im.ene.lab.attiq.data.model.zero.Post;
import im.ene.lab.attiq.ui.activities.ItemDetailActivity;
import im.ene.lab.attiq.ui.activities.ProfileActivity;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.PublicItemsAdapter;
import im.ene.lab.attiq.ui.adapters.RealmListAdapter;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;
import im.ene.lab.attiq.util.AnalyticsUtil;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eneim on 12/14/15.
 */
public class PublicUserHomeFragment extends RealmListFragment<Post> {

  private static final String SCREEN_NAME = "attiq:home:public_items";

  public PublicUserHomeFragment() {

  }

  public static PublicUserHomeFragment newInstance() {
    return new PublicUserHomeFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MoPub.setLocationAwareness(MoPub.LocationAwareness.NORMAL);
  }

  @Override protected void onVisibilityChange(boolean isVisibleToUser) {
    super.onVisibilityChange(isVisibleToUser);
    if (isVisibleToUser) {
      AnalyticsUtil.sendScreenView(SCREEN_NAME);
    }
  }

  @NonNull @Override protected RealmListAdapter<Post> createRealmAdapter() {
    RealmResults<Post> items = mRealm.where(Post.class)
        .findAllSorted("createdAtAsSeconds", Sort.DESCENDING);
    return new PublicItemsWithAdsAdapter(items);
  }

  private Callback mCallback;
  private OnItemClickListener mItemClickListener;
  private MoPubRecyclerAdapter mMopubAdapter;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof Callback) {
      mCallback = (Callback) context;
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mMopubAdapter = new MoPubRecyclerAdapter(getActivity(), mAdapter);
    ViewBinder viewBinder = new ViewBinder.Builder(NativeAdsView.LAYOUT_RES)
        .titleId(NativeAdsView.AD_VIEW_TITLE)
        .iconImageId(NativeAdsView.AD_VIEW_ICON)
        .textId(NativeAdsView.AD_VIEW_TEXT)
        .mainImageId(NativeAdsView.AD_VIEW_IMAGE)
        .build();
    MoPubStaticNativeAdRenderer adViewRenderer = new MoPubStaticNativeAdRenderer(viewBinder);
    mMopubAdapter.registerAdRenderer(adViewRenderer);

    // replace by mMopubAdapter
    mRecyclerView.setAdapter(mMopubAdapter);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
        DividerItemDecoration.VERTICAL_LIST));

    mItemClickListener = new PublicItemsAdapter.OnPublicItemClickListener() {
      @Override public void onUserClick(PublicUser user) {
        startActivity(ProfileActivity.createIntent(getContext(), user.getUrlName()));
      }

      @Override public void onItemContentClick(Post item) {
        startActivity(ItemDetailActivity.createIntent(getContext(), item.getUuid()));
      }
    };

    mAdapter.setOnItemClickListener(mItemClickListener);
    // Optional targeting parameters
    RequestParameters parameters = new RequestParameters.Builder()
        //.keywords("your target words here")
        .build();
    // Request ads when the user returns to this activity
    mMopubAdapter.loadAds(getString(R.string.attiq_mopub_add_id), parameters);

    if (mCallback != null) {
      mCallback.onTimelineCreated(view);
    }
  }

  private static final String TAG = "PublicStreamFragment";

  @Override public void onDestroyView() {
    mMopubAdapter.destroy();
    // no UI interaction after this point;
    mItemClickListener = null;
    super.onDestroyView();
  }

  /**
   * Native Ads on Public timeline
   */
  private static class NativeAdsView {
    private static final int LAYOUT_RES = R.layout.ads_item_view_public;

    private static final int AD_VIEW_TITLE = R.id.ads_title;
    private static final int AD_VIEW_ICON = R.id.ads_icon;
    private static final int AD_VIEW_IMAGE = R.id.ads_image;
    private static final int AD_VIEW_TEXT = R.id.ads_text;
  }

  private class PublicItemsWithAdsAdapter extends PublicItemsAdapter {

    public PublicItemsWithAdsAdapter(RealmResults<Post> items) {
      super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      final ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
      viewHolder.setOnViewHolderClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          int position = viewHolder.getAdapterPosition();
          if (mMopubAdapter != null) {
            position = mMopubAdapter.getOriginalPosition(position);
            if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
              mOnItemClickListener.onItemClick(PublicItemsWithAdsAdapter.this,
                  viewHolder, v, position, getItemId(position));
            }
          }
        }
      });
      return viewHolder;
    }
  }

  public interface Callback {

    /**
     * Public timeline UI created callback
     *
     * @param root
     */
    void onTimelineCreated(View root);
  }
}
