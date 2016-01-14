package im.ene.lab.attiq.data.api;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.one.PublicTag;
import im.ene.lab.attiq.data.request.AccessTokenRequest;
import im.ene.lab.attiq.data.two.AccessToken;
import im.ene.lab.attiq.data.two.Article;
import im.ene.lab.attiq.data.two.Comment;
import im.ene.lab.attiq.data.two.Profile;
import im.ene.lab.attiq.data.two.Tag;
import im.ene.lab.attiq.data.two.User;
import im.ene.lab.attiq.data.zero.FeedItem;
import im.ene.lab.attiq.data.zero.Post;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.PrefUtil;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.UUID;

/**
 * Created by eneim on 12/13/15.
 */
public final class ApiClient {

  private static final OkHttpClient HTTP_CLIENT;
  private static final Retrofit RETROFIT;

  private static final Api.Zero ZERO;
  private static final Api.One ONE;
  private static final Api.Two TWO;

  private static final Converter<ResponseBody, QiitaError> ERROR_CONVERTER;

  public static final int DEFAULT_PAGE_LIMIT = 99; // save API calls...

  static {
    HTTP_CLIENT = Attiq.httpClient().newBuilder()
        .addInterceptor(PrefUtil.ok3Auth())
        .build();

    RETROFIT = new Retrofit.Builder()
        .baseUrl(Api.BASE_URL)
        .client(HTTP_CLIENT)
        .addConverterFactory(GsonConverterFactory.create(IOUtil.gson()))
        .build();

    ZERO = RETROFIT.create(Api.Zero.class);
    ONE = RETROFIT.create(Api.One.class);
    TWO = RETROFIT.create(Api.Two.class);

    ERROR_CONVERTER = RETROFIT.responseBodyConverter(QiitaError.class, new Annotation[0]);
  }

  public static QiitaError parseError(Response response) {
    try {
      return ERROR_CONVERTER.convert(response.errorBody());
    } catch (IOException e) {
      return new QiitaError();
    }
  }

  public static String authCallback() {
    return Attiq.creator().getString(R.string.api_token_auth,
        Attiq.creator().getString(R.string.client_id), UUID.randomUUID().toString());
  }

  public static Call<List<Post>> publicStream(@Nullable Long bottomId) {
    return ZERO.stream(bottomId, "id");
  }

  public static Call<List<FeedItem>> feed(@Nullable Long maxCreatedAt) {
    return ZERO.feed(maxCreatedAt);
  }

  public static Call<List<Post>> openStream(int page, int limit) {
    return ONE.stream(page, limit);
  }

  public static Call<List<Article>> items(int page, int pageLimit, String query) {
    return TWO.searchItems(page, pageLimit, query);
  }

  public static Call<List<Article>> userItems(String userId, int page, int pageLimit) {
    return TWO.userItems(userId, page, pageLimit);
  }

  public static Call<List<Article>> userStockedItems(String userId, int page, int pageLimit) {
    return TWO.userStockedItems(userId, page, pageLimit);
  }

  public static Call<List<Tag>> userFollowingTags(String userId, int page, int pageLimit) {
    return TWO.userFollowingTags(userId, page, pageLimit);
  }

  public static Call<List<PublicTag>> userFollowingTagsV1(String userId, int page, int pageLimit) {
    return ONE.userFollowingTags(userId, page, pageLimit);
  }

  public static Call<List<Article>> tagItems(String tagUrlName, int page, int pageLimit) {
    return TWO.tagItems(tagUrlName, page, pageLimit);
  }

  public static Call<Article> itemDetail(String id) {
    return TWO.itemDetail(id);
  }

  public static Call<List<Tag>> tags(int page) {
    // default page limit: 99
    // default sort type: count
    return TWO.tags(page, DEFAULT_PAGE_LIMIT, "count");
  }

  public static Call<List<Comment>> itemComments(String id) {
    return TWO.comments(id);
  }

  public static Call<AccessToken> accessToken(String code) {
    Resources resources = Attiq.creator().getResources();
    return TWO.accessToken(
        new AccessTokenRequest(
            false,
            resources.getString(R.string.client_id),
            resources.getString(R.string.client_secret),
            code
        )
    );
  }

  public static Call<Profile> me() {
    return TWO.me();
  }

  public static Call<List<Tag>> myTags(int page, int limit) {
    return TWO.tags("", page, limit);
  }

  public static Call<User> user(@NonNull String userName) {
    return TWO.user(userName);
  }

  public static Call<List<Post>> userItems(String userId, int page) {
    return ONE.userItems(userId, page, DEFAULT_PAGE_LIMIT);
  }

  public static Call<List<Post>> userStockedItemsV1(String userId, int page) {
    return ONE.userStockedItems(userId, page, DEFAULT_PAGE_LIMIT);
  }

  public static Call<Void> isStocked(String id) {
    return TWO.getStock(id);
  }

  public static Call<Void> stockItem(String id) {
    return TWO.putStock(id);
  }

  public static Call<Void> unStockItem(String id) {
    return TWO.deleteStock(id);
  }

  public static Call<Void> isFollowing(String userId) {
    return TWO.getFollow(userId);
  }

  public static Call<Void> followUser(String userId) {
    return TWO.putFollow(userId);
  }

  public static Call<Void> unFollowUser(String userId) {
    return TWO.deleteFollow(userId);
  }
}
