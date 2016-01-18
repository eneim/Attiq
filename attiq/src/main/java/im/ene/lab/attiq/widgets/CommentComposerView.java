package im.ene.lab.attiq.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.EditText;

import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.util.ImeUtils;

/**
 * Created by eneim on 1/17/16.
 */
public class CommentComposerView extends ViewPager {

  private static final int MESSAGE_TEXT_CHANGED = 1;

  private View mComposerContainer;
  private View mPreviewerContainer;
  private final EditText mComposer;
  private final MarkdownView mPreviewer;

  private Handler.Callback mHandlerCallback;
  private Handler mHandler;

  private Adapter mAdapter;
  private OnPageChangeListener mPageChangeListener;

  public CommentComposerView(Context context) {
    this(context, null);
  }

  public CommentComposerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mComposerContainer = LayoutInflater.from(context)
        .inflate(R.layout.comment_composer_edittext, this, false);
    mComposer = (EditText) mComposerContainer.findViewById(R.id.composer);

    mPreviewerContainer = LayoutInflater.from(context)
        .inflate(R.layout.comment_composer_preview, this, false);
    mPreviewer = (MarkdownView) mPreviewerContainer.findViewById(R.id.previewer);

    mAdapter = new Adapter(mComposerContainer, mPreviewerContainer);
    setAdapter(mAdapter);

    mPreviewer.setWebChromeClient(new WebChromeClient() {
    });
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mHandlerCallback = new Handler.Callback() {
      @Override public boolean handleMessage(Message msg) {
        if (msg.what == MESSAGE_TEXT_CHANGED) {
          // Update preview
          mPreviewer.loadMarkdown(
              mComposer.getText().toString(),
              "file:///android_asset/html/css/github.css"
          );
        }
        return false;
      }
    };
    mHandler = new Handler(mHandlerCallback);
    mTextChanged = new SimpleTextWatcher() {
      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler.sendEmptyMessageDelayed(MESSAGE_TEXT_CHANGED, 200);
      }
    };
    mComposer.addTextChangedListener(mTextChanged);
    mPageChangeListener = new SimpleOnPageChangeListener() {
      @Override public void onPageSelected(int position) {
        super.onPageSelected(position);
        if (position == 1) {  // preview view
          ImeUtils.hideIme(CommentComposerView.this);
        }
      }
    };
    addOnPageChangeListener(mPageChangeListener);
  }

  public String getComment() {
    return mComposer != null ? mComposer.getText().toString() : null;
  }

  public void clearComment() {
    if (mComposer != null) {
      mComposer.setText("");
    }
  }

  public View getCurrentView() {
    return findViewWithTag("comment_composer:adapter:view:" + getCurrentItem());
  }

  @Override protected void onDetachedFromWindow() {
    mHandler.removeCallbacksAndMessages(null);
    mComposer.removeTextChangedListener(mTextChanged);
    mHandlerCallback = null;
    mTextChanged = null;
    removeOnPageChangeListener(mPageChangeListener);
    mPageChangeListener = null;
    super.onDetachedFromWindow();
  }

  private TextWatcher mTextChanged;

  public static class Adapter extends PagerAdapter {

    private int[] TITLES = {
        R.string.comment_tab_composer,
        R.string.comment_tab_previewer
    };

    private final View[] mViews;

    private Adapter(View... views) {
      if (views.length < 2) {
        throw new IllegalArgumentException("This Adapter requires at least 2 views");
      }
      this.mViews = views;
    }

    @Override public int getCount() {
      return 2;
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
      View view = mViews[position];
      view.setTag("comment_composer:adapter:view:" + position);
      container.addView(view);
      return view;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
      if (isViewFromObject(mViews[position], object)) {
        container.removeView((View) object);
      }
    }

    @Override public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

    @Override public CharSequence getPageTitle(int position) {
      return Attiq.creator().getString(TITLES[position]);
    }
  }
}
