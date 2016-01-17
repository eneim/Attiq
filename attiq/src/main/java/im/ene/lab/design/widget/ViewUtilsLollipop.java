package im.ene.lab.design.widget;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ViewUtilsLollipop {

  static void setBoundsViewOutlineProvider(View view) {
    view.setOutlineProvider(ViewOutlineProvider.BOUNDS);
  }
}
