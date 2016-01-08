package im.ene.lab.attiq.data.api;

import im.ene.lab.attiq.data.one.Post;
import im.ene.lab.attiq.data.request.AccessTokenRequest;
import im.ene.lab.attiq.data.two.AccessToken;
import im.ene.lab.attiq.data.two.Article;
import im.ene.lab.attiq.data.two.Comment;
import im.ene.lab.attiq.data.two.Profile;
import im.ene.lab.attiq.data.two.Tag;
import im.ene.lab.attiq.data.zero.FeedItem;
import im.ene.lab.attiq.data.zero.PublicItem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
interface Api {

  String BASE_URL = "https://qiita.com";

  /**
   * APIs directly get from qiita.com website.
   */
  interface Zero {

    @GET("/api/public") Call<List<PublicItem>> stream(
        @Query("before") Long id,
        @Query("type") String type
    );

    @GET("/api/tracks") Call<List<FeedItem>> feed(
        @Query("max_created_at") Long maxCreatedAt
    );

    @Headers({
        "Accept: application/json",
        "Content-Type: application/json"
    })
    @GET("/{user_name}/stock") Call<List<Post>> stockedItem(
        @Path("user_name") String userId,
        @Query("before") Integer before
    );

  }

  interface One {

    // No token only
    @GET("/api/v1/items") Call<List<PublicItem>> stream(
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v1/users/{url_name}/items") Call<List<Post>> userItems(
        @Path("url_name") String userId,
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v1/users/{url_name}/stocks") Call<List<Post>> userStockedItems(
        @Path("url_name") String userId,
        @Query("page") int page,
        @Query("per_page") int limit
    );
  }

  interface Two {

    @POST("/api/v2/access_tokens") Call<AccessToken> accessToken(
        @Body AccessTokenRequest request
    );

    @GET("/api/v2/authenticated_user") Call<Profile> me();

    @GET("/api/v2/authenticated_user/items") Call<List<Article>> myItems();

    @GET("/api/v2/users/{user_id}/following_tags") Call<List<Tag>> myTags(
        @Path("item_id") String id,
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v2/items") Call<List<Article>> items(
        @Query("page") int page,
        @Query("per_page") int limit,
        @Query("query") String query
    );

    @GET("/api/v2/items/{item_id}") Call<Article> itemDetail(
        @Path("item_id") String id
    );

    @GET("/api/v2/items/{item_id}/comments") Call<List<Comment>> comments(
        @Path("item_id") String id
    );

    @GET("/api/v2/tags") Call<List<Tag>> tags(
        @Query("page") int page,
        @Query("per_page") int limit,
        @Query("sort") String sort
    );

    @GET("/api/v2/users/{user_id}") Call<Profile> user(@Path("user_id") String userName);

    // @GET("/api/v2/items/:item_id/stock") Call<>
  }
}
