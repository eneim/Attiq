/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.support.widget;

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

  private MenuItem menuItem;

  public SmoothActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar,
                                     int openDrawerContentDescRes, int closeDrawerContentDescRes) {
    super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
  }

  /**
   * Specify {@value MenuItem} which is used to close current {@value DrawerLayout}
   *
   * @param drawerLayout which is closed
   * @param gravity      drawerLayout's gravity
   * @param item         item which is clicked to trigger the closing
   */
  public void closeDrawerUsingMenu(DrawerLayout drawerLayout, int gravity, MenuItem item) {
    setMenuItem(item);
    drawerLayout.closeDrawer(gravity);
  }

  public void setMenuItem(MenuItem menuItem) {
    this.menuItem = menuItem;
  }

  @Override public void onDrawerClosed(View drawerView) {
    super.onDrawerClosed(drawerView);
    if (menuItem != null) {
      onDrawerClosedByMenu(drawerView, menuItem);
      menuItem = null;  // reset;
    }
  }

  /**
   * Callback trigged after the DrawerLayout is completely closed
   *
   * @param drawerView
   * @param item
   */
  protected abstract void onDrawerClosedByMenu(View drawerView, @NonNull MenuItem item);

}
