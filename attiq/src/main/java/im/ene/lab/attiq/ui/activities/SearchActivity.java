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

package im.ene.lab.attiq.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.ButterKnife;
import butterknife.OnClick;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.two.User;
import im.ene.lab.attiq.ui.adapters.ArticleListAdapter;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.widgets.BaselineGridTextView;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;
import im.ene.lab.attiq.ui.widgets.EndlessScrollListener;
import im.ene.lab.attiq.util.AnimUtil;
import im.ene.lab.attiq.util.ImeUtil;
import im.ene.lab.attiq.util.UIUtil;
import io.codetail.animation.ViewAnimationUtils;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {

  public static final String EXTRA_MENU_LEFT = "extra_menu_left";
  public static final String EXTRA_MENU_CENTER_X = "extra_menu_center_x";

  private static final int MESSAGE_LOAD_MORE = 1;

  private final Interpolator LINEAR_OUT_SLOW_INT =
      PathInterpolatorCompat.create(0.4f, 0.f, 0.2f, 1.f);

  // UI Components
  @Bind(R.id.searchback) ImageButton mSearchNavButton;
  @Bind(R.id.searchback_container) ViewGroup mSearchNavButtonContainer;
  @Bind(R.id.search_view) SearchView mSearchView;
  @Bind(R.id.search_background) View mSearchBackground;
  @Bind(android.R.id.empty) ProgressBar mProgress;
  @Bind(R.id.search_results) RecyclerView mRecyclerView;
  @Bind(R.id.container) ViewGroup mMainContainer;
  @Bind(R.id.search_toolbar) ViewGroup mSearchToolbar;
  @Bind(R.id.results_container) ViewGroup mResultsContainer;
  @Bind(R.id.scrim) View mScrim;
  // FIXME consider to uncomment this
  // @Bind(R.id.results_scrim) View mResultsScrim;
  @BindInt(R.integer.num_columns) int mColumns;
  @BindDimen(R.dimen.z_app_bar) float mAppBarElevation;
  private BaselineGridTextView mNoResults;
  private Transition mAutoTransition;
  private OnItemClickListener mOnResultItemClick;

  private int mSearchBackDistanceX;
  private int mSearchIconCenterX;
  private Callback<List<Article>> mSearchResultCallback;
  private ArticleListAdapter mAdapter;
  private int mPage;
  private String mQuery;
  private Handler.Callback mHandlerCallback = new Handler.Callback() {
    @Override public boolean handleMessage(Message msg) {
      if (MESSAGE_LOAD_MORE == msg.what) {
        mPage++;
        mAdapter.loadItems(true, mPage, 99, mQuery, mSearchResultCallback);
        Log.d(SearchActivity.class.getSimpleName(), "loadMore: " + mPage);
        return true;
      }
      return false;
    }
  };
  private Handler mHandler = new Handler(mHandlerCallback);
  private int mGridScrollY = 0;
  private RecyclerView.OnScrollListener mOnGridScroll = new RecyclerView.OnScrollListener() {
    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      mGridScrollY += dy;
      if (mGridScrollY > 0 && ViewCompat.getTranslationZ(mSearchToolbar) != mAppBarElevation) {
        ViewCompat.animate(mSearchToolbar)
            .translationZ(mAppBarElevation)
            .setDuration(300L)
            .setInterpolator(LINEAR_OUT_SLOW_INT)
            .start();
      } else if (mGridScrollY == 0 && ViewCompat.getTranslationZ(mSearchToolbar) != 0) {
        ViewCompat.animate(mSearchToolbar)
            .translationZ(0f)
            .setDuration(300L)
            .setInterpolator(LINEAR_OUT_SLOW_INT)
            .start();
      }
    }
  };

  public static Intent createStartIntent(Context context, int menuIconLeft, int menuIconCenterX) {
    Intent starter = new Intent(context, SearchActivity.class);
    starter.putExtra(EXTRA_MENU_LEFT, menuIconLeft);
    starter.putExtra(EXTRA_MENU_CENTER_X, menuIconCenterX);
    return starter;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);
    setupSearchView();
    mAutoTransition = TransitionInflater.from(this)
        .inflateTransition(R.transition.auto)
        .setInterpolator(LINEAR_OUT_SLOW_INT);

    mSearchResultCallback = new Callback<List<Article>>() {
      @Override public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
        List<Article> data = response.body();
        if (!UIUtil.isEmpty(data)) {
          if (mRecyclerView.getVisibility() != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(mMainContainer, mAutoTransition);
            mProgress.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
          }
          mAdapter.addItems(data);
        } else {
          TransitionManager.beginDelayedTransition(mMainContainer, mAutoTransition);
          mProgress.setVisibility(View.GONE);
          setNoResultsVisibility(View.VISIBLE);
        }
      }

      @Override public void onFailure(Call<List<Article>> call, Throwable t) {
      }
    };

    mAdapter = new ArticleListAdapter() {
      @Override
      public void loadItems(boolean isLoadingMore, int page, int pageLimit, @Nullable String query,
          Callback<List<Article>> callback) {
        ApiClient.items(page, pageLimit, query).enqueue(callback);
      }
    };

    mOnResultItemClick = new ArticleListAdapter.OnArticleClickListener() {
      @Override public void onUserClick(User user) {
        startActivity(ProfileActivity.createIntent(SearchActivity.this, user.getId()));
      }

      @Override public void onItemContentClick(Article item) {
        startActivity(ItemDetailActivity.createIntent(SearchActivity.this, item.getId()));
      }
    };

    mAdapter.setOnItemClickListener(mOnResultItemClick);

    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    GridLayoutManager layoutManager = new GridLayoutManager(this, mColumns);
    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        // return mAdapter.getItemColumnSpan(position);
        return mColumns;
      }
    });
    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
      @Override protected void loadMore() {
        mHandler.removeMessages(MESSAGE_LOAD_MORE);
        mHandler.sendEmptyMessageDelayed(MESSAGE_LOAD_MORE, 200);
      }
    });

    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.addOnScrollListener(mOnGridScroll);

    // extract the search icon's location passed from the launching activity, minus 4dp to
    // compensate for different paddings in the views
    mSearchBackDistanceX =
        getIntent().getIntExtra(EXTRA_MENU_LEFT, 0) - (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
    mSearchIconCenterX = getIntent().getIntExtra(EXTRA_MENU_CENTER_X, 0);

    // translate icon to match the launching screen then animate back into position
    mSearchNavButtonContainer.setTranslationX(mSearchBackDistanceX);
    mSearchNavButtonContainer.animate()
        .translationX(0f)
        .setDuration(650L)
        .setInterpolator(LINEAR_OUT_SLOW_INT);
    // change from search icon to back icon
    DrawerArrowDrawable searchToBack = new DrawerArrowDrawable(mSearchNavButton.getContext());
    searchToBack.setDirection(DrawerArrowDrawable.ARROW_DIRECTION_LEFT);
    searchToBack.setProgress(1.f);
    mSearchNavButton.setImageDrawable(searchToBack);

    // fade in the other search chrome
    mSearchBackground.animate().alpha(1f).setDuration(300L).setInterpolator(LINEAR_OUT_SLOW_INT);
    mSearchView.animate()
        .alpha(1f)
        .setStartDelay(400L)
        .setDuration(400L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(new AnimatorListenerAdapter() {
          @Override public void onAnimationEnd(Animator animation) {
            mSearchView.requestFocus();
            ImeUtil.showIme(mSearchView);
          }
        });

    // animate in a mScrim over the content behind
    mScrim.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override public boolean onPreDraw() {
        mScrim.getViewTreeObserver().removeOnPreDrawListener(this);
        AnimatorSet showScrim = new AnimatorSet();
        showScrim.playTogether(ViewAnimationUtils.createCircularReveal(mScrim, mSearchIconCenterX,
                mSearchBackground.getBottom(), 0, (float) Math.hypot(mSearchBackDistanceX,
                    mScrim.getHeight() - mSearchBackground.getBottom())),
            AnimUtil.ofArgb(mScrim, UIUtil.BACKGROUND_COLOR, Color.TRANSPARENT,
                ContextCompat.getColor(SearchActivity.this, R.color.scrim)));
        showScrim.setDuration(400L);
        showScrim.setInterpolator(LINEAR_OUT_SLOW_INT);
        showScrim.start();
        return false;
      }
    });

    onNewIntent(getIntent());
  }

  @Override protected void onPause() {
    // needed to suppress the default window animation when closing the activity
    overridePendingTransition(0, 0);
    super.onPause();
  }

  @Override protected void onDestroy() {
    mOnResultItemClick = null;
    super.onDestroy();
  }

  @Override public void onBackPressed() {
    if (mResultsContainer.getHeight() > 0) {
      clearResults();
      mSearchView.setQuery("", false);
      mSearchView.requestFocus();
      ImeUtil.showIme(mSearchView);
    } else {
      dismiss();
    }
  }

  @Override protected void onNewIntent(Intent intent) {
    if (intent.hasExtra(SearchManager.QUERY)) {
      String query = intent.getStringExtra(SearchManager.QUERY);
      if (!TextUtils.isEmpty(query)) {
        mSearchView.setQuery(query, false);
        searchFor(query);
      }
    }
  }

  private void clearResults() {
    mAdapter.clear();
    TransitionManager.beginDelayedTransition(mMainContainer, mAutoTransition);
    mRecyclerView.setVisibility(View.GONE);
    mProgress.setVisibility(View.GONE);
    // mResultsScrim.setVisibility(View.GONE);
    setNoResultsVisibility(View.GONE);
  }

  @OnClick({ R.id.scrim, R.id.searchback }) protected void dismiss() {
    // if we're showing search mRecyclerView, circular hide them
    if (mResultsContainer.getHeight() > 0) {
      ViewCompat.animate(mResultsContainer)
          .alpha(0.f)
          .setDuration(400L)
          .setInterpolator(LINEAR_OUT_SLOW_INT)
          .start();

      Animator closeResults =
          ViewAnimationUtils.createCircularReveal(mResultsContainer, mSearchIconCenterX, 0,
              (float) Math.hypot(mSearchIconCenterX, mResultsContainer.getHeight()), 0f);
      closeResults.setDuration(500L);
      closeResults.setInterpolator(LINEAR_OUT_SLOW_INT);
      closeResults.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          mResultsContainer.setVisibility(View.INVISIBLE);
        }
      });

      closeResults.start();
    }

    // translate the icon to match position in the launching activity
    ViewCompat.animate(mSearchNavButtonContainer)
        .translationX(mSearchBackDistanceX)
        .alpha(0.f)
        .setDuration(600L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(new ViewPropertyAnimatorListenerAdapter() {
          @Override public void onAnimationEnd(View view) {
            ActivityCompat.finishAfterTransition(SearchActivity.this);
          }
        })
        .start();
    // transform from back icon to search icon
    mSearchNavButton.setImageResource(R.drawable.ic_search_24dp_black);
    // clear the background else the touch ripple moves with the translation which looks bad
    mSearchNavButton.setBackground(null);
    // fade out the other search chrome
    ViewCompat.animate(mSearchView)
        .alpha(0f)
        .setStartDelay(0L)
        .setDuration(120L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(null)
        .start();
    ViewCompat.animate(mSearchBackground)
        .alpha(0f)
        .setStartDelay(300L)
        .setDuration(160L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(null)
        .start();
    if (ViewCompat.getZ(mSearchToolbar) != 0f) {
      ViewCompat.animate(mSearchToolbar)
          .z(0f)
          .setDuration(600L)
          .setInterpolator(LINEAR_OUT_SLOW_INT)
          .start();
    }

    // fade out the mScrim
    ViewCompat.animate(mScrim)
        .alpha(0f)
        .setDuration(400L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(null)
        .start();
  }

  private void setNoResultsVisibility(int visibility) {
    if (visibility == View.VISIBLE) {
      if (mNoResults == null) {
        mNoResults =
            (BaselineGridTextView) ((ViewStub) findViewById(R.id.stub_no_search_results)).inflate();
        mNoResults.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            mSearchView.setQuery("", false);
            mSearchView.requestFocus();
            ImeUtil.showIme(mSearchView);
          }
        });
      }
      String message =
          String.format(getString(R.string.no_search_results), mSearchView.getQuery().toString());
      SpannableStringBuilder ssb = new SpannableStringBuilder(message);
      ssb.setSpan(new StyleSpan(Typeface.ITALIC), message.indexOf('“') + 1, message.length() - 1,
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      mNoResults.setText(ssb);
    }
    if (mNoResults != null) {
      mNoResults.setVisibility(visibility);
    }
  }

  private void setupSearchView() {
    SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
    mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    // hint, inputType & ime options seem to be ignored from XML! Set in code
    mSearchView.setQueryHint(getString(R.string.search_hint));
    mSearchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    mSearchView.setImeOptions(mSearchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
        EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        searchFor(query);
        return true;
      }

      @Override public boolean onQueryTextChange(String query) {
        if (TextUtils.isEmpty(query)) {
          clearResults();
        }
        return true;
      }
    });
    mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View v, boolean hasFocus) {
        Log.d(TAG, "onFocusChange() called with: " + "v = [" + v + "], hasFocus = [" + hasFocus +
            "]");
      }
    });
  }

  private static final String TAG = "SearchActivity";

  private void searchFor(String query) {
    clearResults();
    mProgress.setVisibility(View.VISIBLE);
    ImeUtil.hideIme(mSearchView);
    mSearchView.clearFocus();
    mQuery = query;
    mPage = 1;
    mAdapter.loadItems(false, mPage, 99, mQuery, mSearchResultCallback);
  }

  @Override protected int lookupTheme(UIUtil.Themes themes) {
    return themes == UIUtil.Themes.DARK ? R.style.Attiq_Theme_Dark_NoActionBar_Translucent_Search
        : R.style.Attiq_Theme_Light_NoActionBar_Translucent_Search;
  }
}
