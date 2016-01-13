package im.ene.lab.attiq.activities;

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
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.SearchView;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.ButterKnife;
import butterknife.OnClick;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.adapters.ArticleListAdapter;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.two.Article;
import im.ene.lab.attiq.util.AnimUtils;
import im.ene.lab.attiq.util.ImeUtils;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.BaselineGridTextView;
import im.ene.lab.attiq.widgets.DividerItemDecoration;
import im.ene.lab.attiq.widgets.EndlessScrollListener;
import io.codetail.animation.ViewAnimationUtils;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

//import android.view.ViewAnimationUtils;

public class SearchActivity extends BaseActivity {

  public static final String EXTRA_MENU_LEFT = "EXTRA_MENU_LEFT";
  public static final String EXTRA_MENU_CENTER_X = "EXTRA_MENU_CENTER_X";
  public static final int RESULT_CODE_SAVE = 7;

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
  @Bind(R.id.results_scrim) View mResultsScrim;
  private BaselineGridTextView mNoResults;
  @BindInt(R.integer.num_columns) int mColumns;
  @BindDimen(R.dimen.z_app_bar) float mAppBarElevation;

  private Transition mAutoTransition;

  private static final int MESSAGE_LOADMORE = 1000;

  private Handler.Callback mHandlerCallback = new Handler.Callback() {
    @Override public boolean handleMessage(Message msg) {
      if (MESSAGE_LOADMORE == msg.what) {
        mPage++;
        mAdapter.loadItems(true, mPage, 99, mQuery, mSearchResultCallback);
        Log.d(SearchActivity.class.getSimpleName(), "loadMore: " + mPage);
        return true;
      }
      return false;
    }
  };

  private Handler mHandler = new Handler(mHandlerCallback);

  private int mSearchBackDistanceX;
  private int mSearchIconCenterX;
  // private SearchDataManager mDataManager;
  private Callback<List<Article>> mSearchResultCallback;
  private ArticleListAdapter mAdapter;

  public static Intent createStartIntent(Context context, int menuIconLeft, int menuIconCenterX) {
    Intent starter = new Intent(context, SearchActivity.class);
    starter.putExtra(EXTRA_MENU_LEFT, menuIconLeft);
    starter.putExtra(EXTRA_MENU_CENTER_X, menuIconCenterX);
    return starter;
  }

  private int mPage;
  private String mQuery;

  private final Interpolator LINEAR_OUT_SLOW_INT =
      PathInterpolatorCompat.create(0.4f, 0.f, 0.2f, 1.f);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);
    setupSearchView();
    mAutoTransition = TransitionInflater.from(this)
        .inflateTransition(R.transition.auto).setInterpolator(LINEAR_OUT_SLOW_INT);

    mSearchResultCallback = new Callback<List<Article>>() {
      @Override public void onResponse(Response<List<Article>> response) {
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

      @Override public void onFailure(Throwable t) {
      }
    };

    mAdapter = new ArticleListAdapter() {
      @Override
      public void loadItems(boolean isLoadingMore, int page, int pageLimit,
                            @Nullable String query, Callback<List<Article>> callback) {
        ApiClient.items(page, pageLimit, query).enqueue(callback);
      }
    };

    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
        DividerItemDecoration.VERTICAL_LIST));
    GridLayoutManager layoutManager = new GridLayoutManager(this, mColumns);
    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        // return mAdapter.getItemColumnSpan(position);
        return mColumns;
      }
    });
    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager, 99) {
      @Override protected void loadMore() {
        mHandler.removeMessages(MESSAGE_LOADMORE);
        mHandler.sendEmptyMessageDelayed(MESSAGE_LOADMORE, 200);
      }
    });

    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.addOnScrollListener(mOnGridScroll);

    // extract the search icon's location passed from the launching activity, minus 4dp to
    // compensate for different paddings in the views
    mSearchBackDistanceX = getIntent().getIntExtra(EXTRA_MENU_LEFT, 0) - (int) TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
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
    mSearchBackground.animate()
        .alpha(1f)
        .setDuration(300L)
        .setInterpolator(LINEAR_OUT_SLOW_INT);
    mSearchView.animate()
        .alpha(1f)
        .setStartDelay(400L)
        .setDuration(400L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            mSearchView.requestFocus();
            ImeUtils.showIme(mSearchView);
          }
        });

    // animate in a mScrim over the content behind
    mScrim.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        mScrim.getViewTreeObserver().removeOnPreDrawListener(this);
        AnimatorSet showScrim = new AnimatorSet();
        showScrim.playTogether(
            ViewAnimationUtils.createCircularReveal(
                mScrim,
                mSearchIconCenterX,
                mSearchBackground.getBottom(),
                0,
                (float) Math.hypot(mSearchBackDistanceX, mScrim.getHeight()
                    - mSearchBackground.getBottom())),
            AnimUtils.ofArgb(
                mScrim,
                UIUtil.BACKGROUND_COLOR,
                Color.TRANSPARENT,
                ContextCompat.getColor(SearchActivity.this, R.color.scrim)));
        showScrim.setDuration(400L);
        showScrim.setInterpolator(LINEAR_OUT_SLOW_INT);
        showScrim.start();
        return false;
      }
    });

    onNewIntent(getIntent());
  }

  @Override
  protected void onNewIntent(Intent intent) {
    if (intent.hasExtra(SearchManager.QUERY)) {
      String query = intent.getStringExtra(SearchManager.QUERY);
      if (!TextUtils.isEmpty(query)) {
        mSearchView.setQuery(query, false);
        searchFor(query);
      }
    }
  }

  @Override
  public void onBackPressed() {
    if (mResultsContainer.getHeight() > 0) {
      clearResults();
      mSearchView.setQuery("", false);
      mSearchView.requestFocus();
      ImeUtils.showIme(mSearchView);
    } else {
      dismiss();
    }
  }

  @Override
  protected void onPause() {
    // needed to suppress the default window animation when closing the activity
    overridePendingTransition(0, 0);
    super.onPause();
  }

  @OnClick({R.id.scrim, R.id.searchback})
  protected void dismiss() {

    // if we're showing search mRecyclerView, circular hide them
    if (mResultsContainer.getHeight() > 0) {
      mResultsContainer.animate().alpha(0.f)
          .setDuration(400L)
          .setInterpolator(LINEAR_OUT_SLOW_INT)
          .start();

      Animator closeResults = ViewAnimationUtils.createCircularReveal(
          mResultsContainer,
          mSearchIconCenterX,
          0,
          (float) Math.hypot(mSearchIconCenterX, mResultsContainer.getHeight()),
          0f);
      closeResults.setDuration(500L);
      closeResults.setInterpolator(LINEAR_OUT_SLOW_INT);
      closeResults.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mResultsContainer.setVisibility(View.INVISIBLE);
        }
      });

      closeResults.start();
    }

    // translate the icon to match position in the launching activity
    mSearchNavButtonContainer.animate()
        .translationX(mSearchBackDistanceX)
        .alpha(0.f)
        .setDuration(600L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            ActivityCompat.finishAfterTransition(SearchActivity.this);
          }
        })
        .start();
    // transform from back icon to search icon
    mSearchNavButton.setImageResource(R.drawable.ic_search_24dp_black);
    // clear the background else the touch ripple moves with the translation which looks bad
    mSearchNavButton.setBackground(null);
    // fade out the other search chrome
    mSearchView.animate()
        .alpha(0f)
        .setStartDelay(0L)
        .setDuration(120L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(null)
        .start();
    mSearchBackground.animate()
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
    mScrim.animate()
        .alpha(0f)
        .setDuration(400L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(null)
        .start();
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
      @Override
      public boolean onQueryTextSubmit(String query) {
        searchFor(query);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String query) {
        if (TextUtils.isEmpty(query)) {
          clearResults();
        }
        return true;
      }
    });
    mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {

        }
      }
    });
  }

  private void clearResults() {
    mAdapter.clear();
    TransitionManager.beginDelayedTransition(mMainContainer, mAutoTransition);
    mRecyclerView.setVisibility(View.GONE);
    mProgress.setVisibility(View.GONE);
    mResultsScrim.setVisibility(View.GONE);
    setNoResultsVisibility(View.GONE);
  }

  private void setNoResultsVisibility(int visibility) {
    if (visibility == View.VISIBLE) {
      if (mNoResults == null) {
        mNoResults = (BaselineGridTextView) ((ViewStub)
            findViewById(R.id.stub_no_search_results)).inflate();
        mNoResults.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mSearchView.setQuery("", false);
            mSearchView.requestFocus();
            ImeUtils.showIme(mSearchView);
          }
        });
      }
      String message = String.format(getString(R
          .string.no_search_results), mSearchView.getQuery().toString());
      SpannableStringBuilder ssb = new SpannableStringBuilder(message);
      ssb.setSpan(new StyleSpan(Typeface.ITALIC),
          message.indexOf('“') + 1,
          message.length() - 1,
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      mNoResults.setText(ssb);
    }
    if (mNoResults != null) {
      mNoResults.setVisibility(visibility);
    }
  }

  private void searchFor(String query) {
    clearResults();
    mProgress.setVisibility(View.VISIBLE);
    ImeUtils.hideIme(mSearchView);
    mSearchView.clearFocus();

    mQuery = query;
    mPage = 1;
    mAdapter.loadItems(false, mPage, 99, mQuery, mSearchResultCallback);
  }

  private int mGridScrollY = 0;
  private RecyclerView.OnScrollListener mOnGridScroll = new RecyclerView.OnScrollListener() {
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      mGridScrollY += dy;
      if (mGridScrollY > 0 && ViewCompat.getTranslationZ(mSearchToolbar) != mAppBarElevation) {
        ViewCompat.animate(mSearchToolbar)
            .translationZ(mAppBarElevation)
            .setDuration(300L)
            .setInterpolator(LINEAR_OUT_SLOW_INT)
            .start();
      } else if (mGridScrollY == 0 && ViewCompat.getTranslationZ(mSearchToolbar) != 0) {
        ViewCompat.animate(mSearchToolbar).translationZ(0f)
            .setDuration(300L)
            .setInterpolator(LINEAR_OUT_SLOW_INT)
            .start();
      }
    }
  };
}
