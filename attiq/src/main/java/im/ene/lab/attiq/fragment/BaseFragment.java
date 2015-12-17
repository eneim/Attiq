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

  @CallSuper
  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    EventBus.getDefault().register(this);
    ButterKnife.bind(this, view);
  }

  @CallSuper
  @Override public void onDestroyView() {
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
    super.onDestroyView();
  }

  @SuppressWarnings("Unused")
  public void onEvent(Event event) {
  }
}
