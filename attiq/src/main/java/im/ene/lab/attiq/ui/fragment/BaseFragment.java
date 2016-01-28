package im.ene.lab.attiq.ui.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.util.event.Event;

/**
 * Created by eneim on 12/13/15.
 */
public class BaseFragment extends Fragment {

  private static final String TAG = "BaseFragment";

  private final Visibility mVisibility = new Visibility(getClass().getSimpleName());

  private boolean mIsVisibleToUser = false;

  @Override public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (mIsVisibleToUser != isVisibleToUser) {
      mIsVisibleToUser = isVisibleToUser;
    }

    mVisibility.isVisibleToUser = mIsVisibleToUser;
    EventBus.getDefault().post(mVisibility);
  }

  @CallSuper
  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
  }

  @Override public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
    if (mIsVisibleToUser != getUserVisibleHint()) {
      mIsVisibleToUser = getUserVisibleHint();
    }

    mVisibility.isVisibleToUser = mIsVisibleToUser;
    EventBus.getDefault().post(mVisibility);
  }

  @Override public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @CallSuper
  @Override public void onDestroyView() {
    ButterKnife.unbind(this);
    super.onDestroyView();
  }

  protected void onVisibilityChange(boolean isVisibleToUser) {
    Log.e(TAG, getClass().getSimpleName() + "#onVisibilityChange() called with: "
        + "isVisibleToUser = [" + isVisibleToUser + "]");
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(Visibility visibility) {
    Log.d(TAG, "onEventMainThread() called with: " + "visibility = [" + visibility + "]");
  }

  @SuppressWarnings("unused")
  public void onEvent(Event event) {
  }

  protected String eventTag() {
    return getClass().getSimpleName();
  }

  public boolean isVisibleToUser() {
    return mIsVisibleToUser;
  }

  protected static class Visibility {

    private final String tag;

    public Visibility(String tag) {
      this.tag = tag;
    }

    private boolean isVisibleToUser;
  }
}
