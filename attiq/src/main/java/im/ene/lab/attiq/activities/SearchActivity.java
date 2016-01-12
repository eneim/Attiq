package im.ene.lab.attiq.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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
import im.ene.lab.attiq.data.SearchDataManager;
import im.ene.lab.attiq.data.two.Article;
import im.ene.lab.attiq.util.AnimUtils;
import im.ene.lab.attiq.util.ImeUtils;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.BaselineGridTextView;
import im.ene.lab.attiq.widgets.EndlessScrollListener;
import io.codetail.animation.ViewAnimationUtils;
import retrofit2.Callback;

import java.util.List;

//import android.view.ViewAnimationUtils;

public class SearchActivity extends BaseActivity {

  public static final String EXTRA_MENU_LEFT = "EXTRA_MENU_LEFT";
  public static final String EXTRA_MENU_CENTER_X = "EXTRA_MENU_CENTER_X";
  public static final String EXTRA_QUERY = "EXTRA_QUERY";
  public static final String EXTRA_SAVE_DRIBBBLE = "EXTRA_SAVE_DRIBBBLE";
  public static final String EXTRA_SAVE_DESIGNER_NEWS = "EXTRA_SAVE_DESIGNER_NEWS";
  public static final int RESULT_CODE_SAVE = 7;

  @Bind(R.id.searchback) ImageButton searchBack;
  @Bind(R.id.searchback_container) ViewGroup searchBackContainer;
  @Bind(R.id.search_view) SearchView searchView;
  @Bind(R.id.search_background) View searchBackground;
  @Bind(android.R.id.empty) ProgressBar progress;
  @Bind(R.id.search_results) RecyclerView results;
  @Bind(R.id.container) ViewGroup container;
  @Bind(R.id.search_toolbar) ViewGroup searchToolbar;
  @Bind(R.id.results_container) ViewGroup resultsContainer;
  @Bind(R.id.scrim) View scrim;
  @Bind(R.id.results_scrim) View resultsScrim;
  private BaselineGridTextView noResults;
  @BindInt(R.integer.num_columns) int columns;
  @BindDimen(R.dimen.z_app_bar) float appBarElevation;
  private Transition auto;

  private int searchBackDistanceX;
  private int searchIconCenterX;
  private SearchDataManager dataManager;
  private ArticleListAdapter adapter;

  public static Intent createStartIntent(Context context, int menuIconLeft, int menuIconCenterX) {
    Intent starter = new Intent(context, SearchActivity.class);
    starter.putExtra(EXTRA_MENU_LEFT, menuIconLeft);
    starter.putExtra(EXTRA_MENU_CENTER_X, menuIconCenterX);
    return starter;
  }

  private final Interpolator LINEAR_OUT_SLOW_INT =
      PathInterpolatorCompat.create(0.4f, 0.f, 0.2f, 1.f);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);
    setupSearchView();
    auto = TransitionInflater.from(this)
        .inflateTransition(R.transition.auto).setInterpolator(LINEAR_OUT_SLOW_INT);

    dataManager = new SearchDataManager() {
      @Override public void onDataLoaded(List<Article> data) {
        if (data != null && data.size() > 0) {
          if (results.getVisibility() != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(container, auto);
            progress.setVisibility(View.GONE);
            results.setVisibility(View.VISIBLE);
          }
          adapter.addItems(data);
        } else {
          TransitionManager.beginDelayedTransition(container, auto);
          progress.setVisibility(View.GONE);
          setNoResultsVisibility(View.VISIBLE);
        }
      }
    };

    adapter = new ArticleListAdapter() {
      @Override
      public void loadItems(boolean isLoadingMore, int page, int pageLimit,
                            @Nullable String query, Callback<List<Article>> callback) {

      }
    };

    results.setAdapter(adapter);
    GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        // return adapter.getItemColumnSpan(position);
        return 1;
      }
    });
    results.setLayoutManager(layoutManager);
    results.addOnScrollListener(new EndlessScrollListener(layoutManager, 99) {
      @Override protected void loadMore() {

      }
    });

    results.setHasFixedSize(true);
    results.addOnScrollListener(gridScroll);

    // extract the search icon's location passed from the launching activity, minus 4dp to
    // compensate for different paddings in the views
    searchBackDistanceX = getIntent().getIntExtra(EXTRA_MENU_LEFT, 0) - (int) TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
    searchIconCenterX = getIntent().getIntExtra(EXTRA_MENU_CENTER_X, 0);

    // translate icon to match the launching screen then animate back into position
    searchBackContainer.setTranslationX(searchBackDistanceX);
    searchBackContainer.animate()
        .translationX(0f)
        .setDuration(650L)
        .setInterpolator(LINEAR_OUT_SLOW_INT);
    // transform from search icon to back icon
    DrawerArrowDrawable searchToBack = new DrawerArrowDrawable(searchBack.getContext());
    searchToBack.setDirection(DrawerArrowDrawable.ARROW_DIRECTION_LEFT);
    searchToBack.setProgress(1.f);
    searchBack.setImageDrawable(searchToBack);

    // fade in the other search chrome
    searchBackground.animate()
        .alpha(1f)
        .setDuration(300L)
        .setInterpolator(LINEAR_OUT_SLOW_INT);
    searchView.animate()
        .alpha(1f)
        .setStartDelay(400L)
        .setDuration(400L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            searchView.requestFocus();
            ImeUtils.showIme(searchView);
          }
        });

    // animate in a scrim over the content behind
    scrim.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        scrim.getViewTreeObserver().removeOnPreDrawListener(this);
        AnimatorSet showScrim = new AnimatorSet();
        showScrim.playTogether(
            ViewAnimationUtils.createCircularReveal(
                scrim,
                searchIconCenterX,
                searchBackground.getBottom(),
                0,
                (float) Math.hypot(searchBackDistanceX, scrim.getHeight()
                    - searchBackground.getBottom())),
            AnimUtils.ofArgb(
                scrim,
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
        searchView.setQuery(query, false);
        searchFor(query);
      }
    }
  }

  @Override
  public void onBackPressed() {
    dismiss();
  }

  @Override
  protected void onPause() {
    // needed to suppress the default window animation when closing the activity
    overridePendingTransition(0, 0);
    super.onPause();
  }

  @OnClick({R.id.scrim, R.id.searchback})
  protected void dismiss() {
    // translate the icon to match position in the launching activity
    searchBackContainer.animate()
        .translationX(searchBackDistanceX)
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
    searchBack.setImageResource(R.drawable.ic_search_24dp);
    // clear the background else the touch ripple moves with the translation which looks bad
    searchBack.setBackground(null);
    // fade out the other search chrome
    searchView.animate()
        .alpha(0f)
        .setStartDelay(0L)
        .setDuration(120L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(null)
        .start();
    searchBackground.animate()
        .alpha(0f)
        .setStartDelay(300L)
        .setDuration(160L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(null)
        .start();
    if (ViewCompat.getZ(searchToolbar) != 0f) {
      searchToolbar.animate()
          .z(0f)
          .setDuration(600L)
          .setInterpolator(LINEAR_OUT_SLOW_INT)
          .start();
    }

    // if we're showing search results, circular hide them
    if (resultsContainer.getHeight() > 0) {
      Animator closeResults = ViewAnimationUtils.createCircularReveal(
          resultsContainer,
          searchIconCenterX,
          0,
          (float) Math.hypot(searchIconCenterX, resultsContainer.getHeight()),
          0f);
      closeResults.setDuration(500L);
      closeResults.setInterpolator(LINEAR_OUT_SLOW_INT);
      closeResults.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          resultsContainer.setVisibility(View.INVISIBLE);
        }
      });
      closeResults.start();
    }

    // fade out the scrim
    scrim.animate()
        .alpha(0f)
        .setDuration(400L)
        .setInterpolator(LINEAR_OUT_SLOW_INT)
        .setListener(null)
        .start();
  }

  private void setupSearchView() {
    SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    // hint, inputType & ime options seem to be ignored from XML! Set in code
    searchView.setQueryHint(getString(R.string.search_hint));
    searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
        EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {

        }
      }
    });
  }

  private void clearResults() {
    adapter.clear();
    dataManager.clear();
    TransitionManager.beginDelayedTransition(container, auto);
    results.setVisibility(View.GONE);
    progress.setVisibility(View.GONE);
    resultsScrim.setVisibility(View.GONE);
    setNoResultsVisibility(View.GONE);
  }

  private void setNoResultsVisibility(int visibility) {
    if (visibility == View.VISIBLE) {
      if (noResults == null) {
        noResults = (BaselineGridTextView) ((ViewStub)
            findViewById(R.id.stub_no_search_results)).inflate();
        noResults.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            searchView.setQuery("", false);
            searchView.requestFocus();
            ImeUtils.showIme(searchView);
          }
        });
      }
      String message = String.format(getString(R
          .string.no_search_results), searchView.getQuery().toString());
      SpannableStringBuilder ssb = new SpannableStringBuilder(message);
      ssb.setSpan(new StyleSpan(Typeface.ITALIC),
          message.indexOf('“') + 1,
          message.length() - 1,
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      noResults.setText(ssb);
    }
    if (noResults != null) {
      noResults.setVisibility(visibility);
    }
  }

  private void searchFor(String query) {
    clearResults();
    progress.setVisibility(View.VISIBLE);
    ImeUtils.hideIme(searchView);
    searchView.clearFocus();
    dataManager.searchFor(query);
  }

  private int gridScrollY = 0;
  private RecyclerView.OnScrollListener gridScroll = new RecyclerView.OnScrollListener() {
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      gridScrollY += dy;
      if (gridScrollY > 0 && ViewCompat.getTranslationZ(searchToolbar) != appBarElevation) {
        searchToolbar.animate()
            .translationZ(appBarElevation)
            .setDuration(300L)
            .setInterpolator(LINEAR_OUT_SLOW_INT)
            .start();
      } else if (gridScrollY == 0 && ViewCompat.getTranslationZ(searchToolbar) != 0) {
        searchToolbar.animate()
            .translationZ(0f)
            .setDuration(300L)
            .setInterpolator(LINEAR_OUT_SLOW_INT)
            .start();
      }
    }
  };
}
