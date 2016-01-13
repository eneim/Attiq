package im.ene.lab.attiq.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

import im.ene.lab.attiq.R;

/**
 * Created by eneim on 1/13/16.
 */
public class NotBadImageButton extends AppCompatImageButton {

  public NotBadImageButton(Context context) {
    this(context, null);
  }

  public NotBadImageButton(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NotBadImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TypedArray a = context.obtainStyledAttributes(attrs,
        R.styleable.NotBadImageButton, defStyleAttr, 0);
    final float elevation = a.getDimension(R.styleable.NotBadImageButton_elevation, 0f);
    a.recycle();

    ViewCompat.setElevation(this, elevation);
  }
}
