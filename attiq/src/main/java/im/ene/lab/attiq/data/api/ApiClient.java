package im.ene.lab.attiq.data.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.model.one.PublicTag;
import im.ene.lab.attiq.data.model.two.AccessToken;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.two.Comment;
import im.ene.lab.attiq.data.model.two.Profile;
import im.ene.lab.attiq.data.model.two.Tag;
import im.ene.lab.attiq.data.model.two.User;
import im.ene.lab.attiq.data.model.zero.FeedItem;
import im.ene.lab.attiq.data.model.zero.PublicPost;
import im.ene.lab.attiq.data.request.AccessTokenRequest;
import im.ene.lab.attiq.data.request.PostCommentRequest;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.PrefUtil;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by eneim on 12/13/15.
 */
public final class ApiClient {

  private static final OkHttpClient sHttpClient;
  private static final Retrofit sRetrofit;

  private static final Api.Zero sZero;
  private static final Api.One sOne;
  private static final Api.Two sTwo;

  private static final Converter<ResponseBody, QiitaError> sErrorConverter;

  public static final int DEFAULT_PAGE_LIMIT = 99; // save API calls..., may reduce performance

  static {
    sHttpClient = Attiq.httpClient().newBuilder().addInterceptor(PrefUtil.ok3Auth()).build();

    sRetrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL)
        .client(sHttpClient)
        .addConverterFactory(GsonConverterFactory.create(IOUtil.gson()))
        .build();

    sZero = sRetrofit.create(Api.Zero.class);
    sOne = sRetrofit.create(Api.One.class);
    sTwo = sRetrofit.create(Api.Two.class);

    // Convert Error from API to usable POJO
    sErrorConverter = sRetrofit.responseBodyConverter(QiitaError.class, new Annotation[0]);
  }

  public static QiitaError parseError(Response response) {
    try {
      return sErrorConverter.convert(response.errorBody());
    } catch (IOException e) {
      return QiitaError.unknown();
    }
  }

  public static String authCallback() {
    // Directly access Application resource to get String
    return Attiq.creator()
        .getString(R.string.api_token_auth, Attiq.creator().getString(R.string.client_id),
            UUID.randomUUID().toString());
  }

  public static Call<List<PublicPost>> publicStream(@Nullable Long bottomId) {
    return sZero.stream(bottomId, "id");
  }

  public static Call<List<FeedItem>> feed(@Nullable Long maxCreatedAt) {
    return sZero.feed(maxCreatedAt);
  }

  public static Call<List<PublicPost>> openStream(int page, int limit) {
    return sOne.stream(page, limit);
  }

  public static Call<List<Article>> items(int page, int pageLimit, String query) {
    return sTwo.searchItems(page, pageLimit, query);
  }

  public static Call<List<Article>> userItems(String userId, int page, int pageLimit) {
    return sTwo.userItems(userId, page, pageLimit);
  }

  public static Call<List<Article>> userStockedItems(String userId, int page, int pageLimit) {
    return sTwo.userStockedItems(userId, page, pageLimit);
  }

  public static Call<List<Tag>> userFollowingTags(String userId, int page, int pageLimit) {
    return sTwo.userFollowingTags(userId, page, pageLimit);
  }

  public static Call<List<PublicTag>> userFollowingTagsV1(String userId, int page, int pageLimit) {
    return sOne.userFollowingTags(userId, page, pageLimit);
  }

  public static Call<List<Article>> tagItems(String tagUrlName, int page, int pageLimit) {
    return sTwo.tagItems(tagUrlName, page, pageLimit);
  }

  public static Call<Article> itemDetail(String id) {
    return sTwo.itemDetail(id);
  }

  public static Call<List<Tag>> tags(int page) {
    // default page limit: 99
    // default sort type: count
    return sTwo.tags(page, DEFAULT_PAGE_LIMIT, "count");
  }

  public static Call<ArrayList<Comment>> itemComments(String id) {
    return sTwo.comments(id);
  }

  public static Call<AccessToken> accessToken(String code) {
    return sTwo.accessToken(new AccessTokenRequest(Attiq.creator().getString(R.string.client_id),
        Attiq.creator().getString(R.string.client_secret), code));
  }

  // Self profile
  public static Call<Profile> me() {
    return sTwo.me();
  }

  public static Call<List<Tag>> myTags(int page, int limit) {
    return sTwo.tags("", page, limit);
  }

  public static Call<User> user(@NonNull String userName) {
    return sTwo.user(userName);
  }

  public static Call<List<PublicPost>> userItems(String userId, int page) {
    return sOne.userItems(userId, page, DEFAULT_PAGE_LIMIT);
  }

  public static Call<List<PublicPost>> userStockedItemsV1(String userId, int page) {
    return sOne.userStockedItems(userId, page, DEFAULT_PAGE_LIMIT);
  }

  public static Call<Void> isStocked(String id) {
    return sTwo.getStock(id);
  }

  public static Call<Void> stockItem(String id) {
    return sTwo.putStock(id);
  }

  public static Call<Void> unStockItem(String id) {
    return sTwo.deleteStock(id);
  }

  public static Call<Void> isFollowing(String userId) {
    return sTwo.getFollow(userId);
  }

  public static Call<Void> followUser(String userId) {
    return sTwo.putFollow(userId);
  }

  public static Call<Void> unFollowUser(String userId) {
    return sTwo.deleteFollow(userId);
  }

  public static Call<Comment> postComment(String itemId, String comment) {
    return sTwo.postComment(itemId, new PostCommentRequest(comment));
  }

  public static Call<Void> deleteComment(String commentId) {
    return sTwo.deleteComment(commentId);
  }

  public static Call<Comment> patchComment(String commentId, String body) {
    return sTwo.patchComment(commentId, new PostCommentRequest(body));
  }

  public static Call<Void> getTagFollowState(String tagId) {
    return sTwo.getTagFollowState(tagId);
  }

  public static Call<Void> followTag(String tagId) {
    return sTwo.followTag(tagId);
  }

  public static Call<Void> unFollowTag(String tagId) {
    return sTwo.unFollowTag(tagId);
  }
}
