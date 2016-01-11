package im.ene.lab.attiq.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.TextViewCompat;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by eneim on 1/11/16.
 */
public abstract class TextViewTarget implements Target {

  private final TextView textView;

  public TextViewTarget(TextView textView) {
    this.textView = textView;
  }

  public abstract void onBitmapLoaded(TextView textView, Bitmap bitmap, Picasso.LoadedFrom from);

  @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
    if (textView != null) {
      onBitmapLoaded(textView, bitmap, from);
    }
  }

  @Override public void onBitmapFailed(Drawable errorDrawable) {
    if (textView != null) {
      TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(textView,
          errorDrawable, null, null, null);
    }
  }

  @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
    if (textView != null) {
      TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(textView,
          placeHolderDrawable, null, null, null);
    }
  }
}
