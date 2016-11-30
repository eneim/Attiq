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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;

/**
 * Created by eneim on 12/14/15.
 * <p/>
 * Fragment which will be added to {@link im.ene.lab.attiq.ui.activities.HomeActivity} if current
 * User is authorized
 */
public class AuthorizedUserHomeFragment extends BaseFragment {

  private static final String ARG_USER_ID = "fragment_arg_user_id";

  public static AuthorizedUserHomeFragment newInstance(@NonNull String userId) {
    Bundle args = new Bundle();
    args.putString(ARG_USER_ID, userId);
    AuthorizedUserHomeFragment fragment = new AuthorizedUserHomeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof Callback) {
      mCallback = (Callback) context;
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUserId = getArguments() != null ? getArguments().getString(ARG_USER_ID) : null;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
      savedInstanceState) {
    return inflater.inflate(R.layout.fragment_viewpager, container, false);
  }

  private Callback mCallback;
  private ViewPager mViewPager;
  private String mUserId;

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
    mViewPager.setOffscreenPageLimit(3);
    MainPagerAdapter pagerAdapter = new MainPagerAdapter(mUserId, getChildFragmentManager());
    mViewPager.setAdapter(pagerAdapter);

    if (mCallback != null) {
      mCallback.onUserHomeCreated(mViewPager);
    }
  }

  public interface Callback {

    /**
     * User specific UI created callback
     *
     * @param viewPager
     */
    void onUserHomeCreated(ViewPager viewPager);
  }

  private static class MainPagerAdapter extends FragmentStatePagerAdapter {

    private static final int TITLES[] = {
        R.string.tab_home_public,
        R.string.tab_home_feed,
        R.string.tab_title_stocks
        // , R.string.tab_title_history
    };
    private final String mUserId;

    public MainPagerAdapter(String userId, FragmentManager fm) {
      super(fm);
      mUserId = userId;
    }

    @Override public Fragment getItem(int position) {
      if (position == 0) {
        return PublicStreamFragment.newInstance();
      } else if (position == 1) {
        return FeedListFragment.newInstance();
      } else if (position == 2) {
        return UserStockedArticlesFragment.newInstance(mUserId);
      } else if (position == 3) {
        return HistoryFragment.newInstance();
      }

      return PublicStreamFragment.newInstance();
    }

    @Override public int getCount() {
      if (mUserId != null) {
        return TITLES.length;
      } else {
        return TITLES.length - 1;
      }
    }

    @Override public CharSequence getPageTitle(int position) {
      // Directly use Application Context here. There is no style or theme here so we don't care
      return Attiq.creator().getString(TITLES[position]);
    }
  }

}
