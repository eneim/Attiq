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
import im.ene.lab.attiq.data.model.zero.PublicPost;
import im.ene.lab.attiq.ui.activities.ItemDetailActivity;
import im.ene.lab.attiq.ui.activities.ProfileActivity;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.PublicItemsAdapter;
import im.ene.lab.attiq.ui.adapters.RealmListAdapter;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.EnumSet;

/**
 * Created by eneim on 12/14/15.
 */
public class PublicStreamFragment extends RealmListFragment<PublicPost> {

  private static final String SCREEN_NAME = "attiq:home:public_items";

  public PublicStreamFragment() {

  }

  public static PublicStreamFragment newInstance() {
    return new PublicStreamFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MoPub.setLocationAwareness(MoPub.LocationAwareness.NORMAL);
  }

  @NonNull @Override protected RealmListAdapter<PublicPost> createRealmAdapter() {
    RealmResults<PublicPost> items =
        mRealm.where(PublicPost.class).findAllSorted("createdAtAsSeconds", Sort.DESCENDING);
    return new PublicItemsWithAdsAdapter(items);
  }

  private OnItemClickListener mItemClickListener;

  private MoPubRecyclerAdapter mMopubAdapter;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

    mMopubAdapter = new MoPubRecyclerAdapter(getActivity(), mAdapter);
    ViewBinder viewBinder =
        new ViewBinder.Builder(NativeAdsView.LAYOUT_RES).titleId(NativeAdsView.AD_VIEW_TITLE)
            .iconImageId(NativeAdsView.AD_VIEW_ICON)
            .textId(NativeAdsView.AD_VIEW_TEXT)
            .mainImageId(NativeAdsView.AD_VIEW_IMAGE)
            .privacyInformationIconImageId(NativeAdsView.AD_PRIVACY_INFO_ICON)
            .build();
    MoPubStaticNativeAdRenderer adViewRenderer = new MoPubStaticNativeAdRenderer(viewBinder);
    mMopubAdapter.registerAdRenderer(adViewRenderer);

    // replace by mMopubAdapter
    mRecyclerView.setAdapter(mMopubAdapter);

    mItemClickListener = new PublicItemsAdapter.OnPublicItemClickListener() {
      @Override public void onUserClick(PublicUser user) {
        startActivity(ProfileActivity.createIntent(getContext(), user.getUrlName()));
      }

      @Override public void onItemContentClick(PublicPost item) {
        startActivity(ItemDetailActivity.createIntent(getContext(), item.getUuid()));
      }
    };

    mAdapter.setOnItemClickListener(mItemClickListener);
    // Optional targeting parameters
    final EnumSet<RequestParameters.NativeAdAsset> desiredAssets =
        EnumSet.of(RequestParameters.NativeAdAsset.TITLE, RequestParameters.NativeAdAsset.TEXT,
            RequestParameters.NativeAdAsset.ICON_IMAGE, RequestParameters.NativeAdAsset.MAIN_IMAGE,
            RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);

    RequestParameters parameters = new RequestParameters.Builder()
        //.keywords("your target words here")
        .desiredAssets(desiredAssets).build();
    // Request ads when the user returns to this activity

    // TODO comment out to publish and apply for Mopub Native Ads
    // mMopubAdapter.loadAds(getString(R.string.attiq_mopub_add_id), parameters);
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
    private static final int AD_PRIVACY_INFO_ICON = R.id.ads_privacy_information_icon_image;
  }

  private class PublicItemsWithAdsAdapter extends PublicItemsAdapter {

    public PublicItemsWithAdsAdapter(RealmResults<PublicPost> items) {
      super(items);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      final ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
      viewHolder.setOnViewHolderClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          int position = viewHolder.getAdapterPosition();
          if (mMopubAdapter != null) {
            try {
              position = mMopubAdapter.getOriginalPosition(position);
              if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(PublicItemsWithAdsAdapter.this, viewHolder, v,
                    position, getItemId(position));
              }
            } catch (Exception er) {
              er.printStackTrace();
            }
          }
        }
      });
      return viewHolder;
    }
  }
}
