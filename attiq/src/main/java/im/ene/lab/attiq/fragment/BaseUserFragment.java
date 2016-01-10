package im.ene.lab.attiq.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by eneim on 1/10/16.
 */
public class BaseUserFragment extends BaseFragment {

  protected static final String ARGS_USER_ID = "attiq_fragment_args_user_id";

  protected String mUserId;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mUserId = getArguments().getString(ARGS_USER_ID);
    }
  }
}
