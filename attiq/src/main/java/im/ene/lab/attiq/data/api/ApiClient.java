package im.ene.lab.attiq.data.api;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;

import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.open.Profile;
import im.ene.lab.attiq.data.api.v1.response.PublicItem;
import im.ene.lab.attiq.data.api.v2.request.AccessTokenRequest;
import im.ene.lab.attiq.data.api.v2.response.AccessToken;
import im.ene.lab.attiq.data.api.v2.response.Article;
import im.ene.lab.attiq.data.api.v2.response.Comment;
import im.ene.lab.attiq.data.api.v2.response.Tag;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.PrefUtil;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import java.util.List;
import java.util.UUID;

/**
 * Created by eneim on 12/13/15.
 */
public final class ApiClient {

  private static final OkHttpClient HTTP_CLIENT;
  private static final Retrofit RETROFIT;

  private static final Api.Open OPEN;
  private static final Api.Items ITEMS;
  private static final Api.Me SELF;

  static {
    HTTP_CLIENT = new OkHttpClient();
    HTTP_CLIENT.networkInterceptors().add(new StethoInterceptor());
    HTTP_CLIENT.interceptors().add(PrefUtil.authInterceptor());
    RETROFIT = new Retrofit.Builder()
        .baseUrl(Api.BASE_URL)
        .client(HTTP_CLIENT)
        .addConverterFactory(GsonConverterFactory.create(IOUtil.gson()))
        .build();

    OPEN = RETROFIT.create(Api.Open.class);
    ITEMS = RETROFIT.create(Api.Items.class);
    SELF = RETROFIT.create(Api.Me.class);
  }

  public static OkHttpClient client() {
    return HTTP_CLIENT;
  }

  public static String authCallback(@NonNull Context context) {
    return context.getString(R.string.api_token_auth,
        context.getString(R.string.client_id), UUID.randomUUID().toString());
  }

  public static Call<List<PublicItem>> stream(@Nullable Long bottomId) {
    if (bottomId != null) {
      return OPEN.stream(bottomId, "id");
    } else {
      return OPEN.stream();
    }
  }

  public static Call<List<PublicItem>> stream(int page, int limit) {
    return ITEMS.stream(page, limit);
  }

  public static Call<List<Article>> items(int page, int pageLimit, String query) {
    return ITEMS.items(page, pageLimit, query);
  }

  public static Call<Article> itemDetail(String id) {
    return ITEMS.itemDetail(id);
  }

  public static Call<List<Tag>> tags(int page) {
    // default page limit: 99
    // default sort type: count
    return ITEMS.tags(page, 99, "count");
  }

  public static Call<List<Comment>> itemComments(String id) {
    return ITEMS.comments(id);
  }

  public static Call<AccessToken> accessToken(String code) {
    Resources resources = Attiq.creator().getResources();
    return SELF.accessToken(
        new AccessTokenRequest(
            resources.getString(R.string.client_id),
            resources.getString(R.string.client_secret),
            code
        )
    );
  }

  public static Call<Profile> me() {
    return SELF.me();
  }

  public static Call<List<Tag>> myTags(int page, int limit) {
    return SELF.myTags("", page, limit);
  }
}
