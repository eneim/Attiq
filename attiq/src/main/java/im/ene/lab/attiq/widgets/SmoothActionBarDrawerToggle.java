package im.ene.lab.attiq.widgets;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by eneim on 1/15/16.
 */
public abstract class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

  public SmoothActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int
      openDrawerContentDescRes, int closeDrawerContentDescRes) {
    super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
  }

  public SmoothActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar,
                                     int openDrawerContentDescRes, int closeDrawerContentDescRes) {
    super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
  }

  private MenuItem menuItem;

  public void setMenuItem(MenuItem menuItem) {
    this.menuItem = menuItem;
  }

  public void closeDrawer(DrawerLayout drawerLayout, int gravity, MenuItem item) {
    setMenuItem(item);
    drawerLayout.closeDrawer(gravity);
  }

  @Override public void onDrawerClosed(View drawerView) {
    super.onDrawerClosed(drawerView);
    if (menuItem != null) {
      onDrawerClosedByMenu(drawerView, menuItem);
      menuItem = null;  // reset;
    }
  }

  protected abstract void onDrawerClosedByMenu(View drawerView, @NonNull MenuItem item);

}
