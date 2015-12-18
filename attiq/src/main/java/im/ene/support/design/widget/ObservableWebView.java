package im.ene.support.design.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by eneim on 12/19/15.
 */
public class ObservableWebView extends WebView {

  public ObservableWebView(Context context) {
    super(context);
  }

  public ObservableWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ObservableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if (mListener != null) {
      mListener.onScrollChange(this, l, t, oldl, oldt);
    }
  }

  private OnScrollChangeListener mListener;

  public void setOnScrollObservedListener(OnScrollChangeListener listener) {
    mListener = listener;
  }

  public interface OnScrollChangeListener {
    /**
     * Called when the scroll position of a view changes.
     *
     * @param v          The view whose scroll position has changed.
     * @param scrollX    Current horizontal scroll origin.
     * @param scrollY    Current vertical scroll origin.
     * @param oldScrollX Previous horizontal scroll origin.
     * @param oldScrollY Previous vertical scroll origin.
     */
    void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
  }
}
