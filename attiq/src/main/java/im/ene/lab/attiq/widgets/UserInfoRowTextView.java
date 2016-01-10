package im.ene.lab.attiq.widgets;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import im.ene.lab.attiq.R;

/**
 * Created by eneim on 1/10/16.
 */
public class UserInfoRowTextView extends RelativeLayout {

  public UserInfoRowTextView(Context context) {
    this(context, null);
  }

  public UserInfoRowTextView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  @Bind(R.id.icon) ImageView mIcon;
  @Bind(R.id.text) TextView mText;

  public UserInfoRowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    LayoutInflater.from(context).inflate(R.layout.widget_user_infor_text, this, true);
    ButterKnife.bind(this, this);
    mIcon.setVisibility(GONE);
  }

  public void setIcon(@DrawableRes int icon) {
    if (mIcon.getVisibility() == GONE) {
      mIcon.setVisibility(VISIBLE);
    }

    mIcon.setImageResource(icon);
  }

  @Deprecated
  public void setIcon(BitmapDrawable icon) {
    mIcon.setImageDrawable(icon);
  }

  public void setText(@StringRes int text) {
    mText.setText(text);
  }

  public void setText(CharSequence text) {
    mText.setText(text);
  }
}
