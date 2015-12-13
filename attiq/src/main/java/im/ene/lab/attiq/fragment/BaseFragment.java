package im.ene.lab.attiq.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.data.event.Event;

/**
 * Created by eneim on 12/13/15.
 */
public class BaseFragment extends Fragment {

  protected EventBus mEventBus;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mEventBus = new EventBus();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mEventBus = null;
  }

  @CallSuper
  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mEventBus.register(this);
    ButterKnife.bind(this, view);
  }

  @CallSuper
  @Override public void onDestroyView() {
    ButterKnife.unbind(this);
    mEventBus.unregister(this);
    super.onDestroyView();
  }

  @SuppressWarnings("Unused")
  public void onEvent(Event event) {
  }
}
