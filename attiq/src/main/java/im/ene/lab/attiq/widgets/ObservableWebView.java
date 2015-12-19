package im.ene.lab.attiq.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by eneim on 12/20/15.
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
      mListener.onScrolled(this, l, t, oldl, oldt);
    }
  }

  private OnScrollListener mListener;

  public void setOnScrollListener(OnScrollListener listener) {
    mListener = listener;
  }

  public interface OnScrollListener {

    void onScrolled(View view, int scrollX, int scrollY, int oldX, int oldY);
  }
}
