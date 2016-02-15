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
import android.view.View;
import im.ene.lab.attiq.data.model.one.PublicUser;
import im.ene.lab.attiq.data.model.zero.PublicPost;
import im.ene.lab.attiq.ui.activities.ItemDetailActivity;
import im.ene.lab.attiq.ui.activities.ProfileActivity;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.PublicItemsAdapter;
import im.ene.lab.attiq.ui.adapters.AttiqRealmListAdapter;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;
import im.ene.lab.attiq.util.PrefUtil;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eneim on 12/14/15.
 */
public class PublicUserHomeFragment extends RealmListFragment<PublicPost> {

  private static final String SCREEN_NAME = "attiq:home:public_items";

  public PublicUserHomeFragment() {

  }

  public static PublicUserHomeFragment newInstance() {
    return new PublicUserHomeFragment();
  }

  @NonNull @Override protected AttiqRealmListAdapter<PublicPost> createRealmAdapter() {
    RealmResults<PublicPost> items = mRealm.where(PublicPost.class)
        .findAllSorted("createdAtAsSeconds", Sort.DESCENDING);
    return new PublicItemsAdapter(items);
  }

  private Callback mCallback;
  private OnItemClickListener mItemClickListener;
  // private MoPubRecyclerAdapter mMopubAdapter;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof Callback) {
      mCallback = (Callback) context;
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
        DividerItemDecoration.VERTICAL_LIST));

    mItemClickListener = new PublicItemsAdapter.OnPublicItemClickListener() {
      @Override public void onUserClick(PublicUser user) {
        if (PrefUtil.checkNetwork(getContext())) {
          startActivity(ProfileActivity.createIntent(getContext(), user.getUrlName()));
        }
      }

      @Override public void onItemContentClick(PublicPost item) {
        if (PrefUtil.checkNetwork(getContext())) {
          startActivity(ItemDetailActivity.createIntent(getContext(), item.getUuid()));
        }
      }
    };

    mAdapter.setOnItemClickListener(mItemClickListener);
    if (mCallback != null) {
      mCallback.onTimelineCreated(view);
    }
  }

  private static final String TAG = "PublicStreamFragment";

  @Override public void onDestroyView() {
    // no UI interaction after this point;
    mItemClickListener = null;
    super.onDestroyView();
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
