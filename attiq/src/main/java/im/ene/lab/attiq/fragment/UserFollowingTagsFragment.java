package im.ene.lab.attiq.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wefika.flowlayout.FlowLayout;

import butterknife.Bind;
import im.ene.lab.attiq.R;

/**
 * Created by eneim on 1/10/16.
 */
public class UserFollowingTagsFragment extends BaseUserFragment {

  public static UserFollowingTagsFragment newInstance(String userId) {
    UserFollowingTagsFragment fragment = new UserFollowingTagsFragment();
    Bundle args = new Bundle();
    args.putString(ARGS_USER_ID, userId);
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
      savedInstanceState) {
    return inflater.inflate(R.layout.fragment_user_tags, container, false);
  }

  @Bind(R.id.user_tags_container) FlowLayout mTagContainer;

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }


}
