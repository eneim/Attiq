package im.ene.lab.attiq.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by eneim on 1/6/16.
 */
public class DummyFragment extends BaseFragment {

  public static DummyFragment newInstance() {
    return new DummyFragment();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
      savedInstanceState) {
    TextView textView = (TextView) LayoutInflater.from(container.getContext())
        .inflate(android.R.layout.simple_list_item_1, container, false);
    textView.setText("Dummy");
    return textView;
  }
}
