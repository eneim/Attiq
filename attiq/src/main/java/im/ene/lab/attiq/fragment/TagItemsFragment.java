package im.ene.lab.attiq.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import im.ene.lab.attiq.adapters.ListAdapter;
import im.ene.lab.attiq.adapters.TagItemsAdapter;
import im.ene.lab.attiq.data.two.Article;

/**
 * Created by eneim on 1/10/16.
 */
public class TagItemsFragment extends ListFragment<Article> {

  private static final String ARGS_TAG_ID = "attiq_fragment_args_tag_id";

  private String mTagId;

  public static TagItemsFragment newInstance(String tagId) {
    TagItemsFragment fragment = new TagItemsFragment();
    Bundle args = new Bundle();
    args.putString(ARGS_TAG_ID, tagId);
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override protected ListAdapter<Article> createAdapter() {
    return new TagItemsAdapter(mTagId);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mTagId = getArguments().getString(ARGS_TAG_ID);
    }
  }
}
