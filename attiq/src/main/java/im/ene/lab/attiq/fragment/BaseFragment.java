package im.ene.lab.attiq.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.util.event.Event;

/**
 * Created by eneim on 12/13/15.
 */
public class BaseFragment extends Fragment {

  @CallSuper
  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
  }

  @Override public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
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

  @SuppressWarnings("unused")
  public void onEvent(Event event) {
  }
}
