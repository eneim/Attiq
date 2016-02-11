package im.ene.lab.attiq.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.two.User;
import im.ene.lab.attiq.ui.activities.ItemDetailActivity;
import im.ene.lab.attiq.ui.activities.ProfileActivity;
import im.ene.lab.attiq.ui.adapters.ArticleListAdapter;
import im.ene.lab.attiq.ui.adapters.ListAdapter;
import im.ene.lab.attiq.ui.adapters.OnItemClickListener;
import im.ene.lab.attiq.ui.adapters.TagItemsAdapter;
import im.ene.lab.attiq.ui.widgets.DividerItemDecoration;
import im.ene.lab.attiq.util.PrefUtil;
import java.util.List;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by eneim on 1/10/16.
 */
public class TagItemsFragment extends ListFragment<Article> {

  private static final String ARGS_TAG_ID = "attiq_fragment_args_tag_id";

  private static final String SCREEN_NAME = "attiq:user:tag_items";

  private String mTagId;
  private OnItemClickListener mOnItemClickListener;
  private Callback mCallback;

  public static TagItemsFragment newInstance(String tagId) {
    TagItemsFragment fragment = new TagItemsFragment();
    Bundle args = new Bundle();
    args.putString(ARGS_TAG_ID, tagId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof Callback) {
      mCallback = (Callback) context;
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mTagId = getArguments().getString(ARGS_TAG_ID);
    }
  }

  @Override public void onDetach() {
    mCallback = null;
    super.onDetach();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
        DividerItemDecoration.VERTICAL_LIST));

    mOnItemClickListener = new ArticleListAdapter.OnArticleClickListener() {
      @Override public void onUserClick(User user) {
        if (PrefUtil.checkNetwork(getContext())) {
          startActivity(ProfileActivity.createIntent(getContext(), user.getId()));
        }
      }

      @Override public void onItemContentClick(Article item) {
        if (PrefUtil.checkNetwork(getContext())) {
          startActivity(ItemDetailActivity.createIntent(getContext(), item.getId()));
        }
      }
    };

    mAdapter.setOnItemClickListener(mOnItemClickListener);
  }

  @Override public void onDestroyView() {
    mOnItemClickListener = null;
    super.onDestroyView();
  }

  @NonNull @Override protected ListAdapter<Article> createAdapter() {
    return new TagItemsAdapter(mTagId);
  }

  @Override public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
    if (mCallback != null) {
      mCallback.onResponseHeaders(response.headers());
    }
    super.onResponse(call, response);
  }

  public interface Callback {

    void onResponseHeaders(Headers headers);
  }
}
