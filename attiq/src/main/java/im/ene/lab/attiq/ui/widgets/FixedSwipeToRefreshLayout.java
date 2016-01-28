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

package im.ene.lab.attiq.ui.widgets;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * Created by eneim on 1/28/16.
 */
public class FixedSwipeToRefreshLayout extends SwipeRefreshLayout {
  public FixedSwipeToRefreshLayout(Context context) {
    super(context);
  }

  public FixedSwipeToRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  private boolean mMeasured = false;
  private boolean mPreMeasureRefreshing = false;

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (!mMeasured) {
      mMeasured = true;
      setRefreshing(mPreMeasureRefreshing);
    }
  }

  @Override
  public void setRefreshing(boolean refreshing) {
    if (mMeasured) {
      super.setRefreshing(refreshing);
    } else {
      mPreMeasureRefreshing = refreshing;
    }
  }
}
