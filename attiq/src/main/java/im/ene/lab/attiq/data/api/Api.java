package im.ene.lab.attiq.data.api;

import im.ene.lab.attiq.data.model.one.PublicTag;
import im.ene.lab.attiq.data.request.AccessTokenRequest;
import im.ene.lab.attiq.data.request.PostCommentRequest;
import im.ene.lab.attiq.data.model.two.AccessToken;
import im.ene.lab.attiq.data.model.two.Article;
import im.ene.lab.attiq.data.model.two.Comment;
import im.ene.lab.attiq.data.model.two.Profile;
import im.ene.lab.attiq.data.model.two.Tag;
import im.ene.lab.attiq.data.model.two.User;
import im.ene.lab.attiq.data.model.zero.FeedItem;
import im.ene.lab.attiq.data.model.zero.PublicPost;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
final class Api {

  static String BASE_URL = "https://qiita.com";

  /**
   * APIs directly get from qiita.com website.
   */
  interface Zero {

    @GET("/api/public") Call<List<PublicPost>> stream(
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
    @GET("/{user_name}/stock") Call<List<PublicPost>> stockedItem(
        @Path("user_name") String userId,
        @Query("before") Integer before
    );

  }

  interface One {

    // No token only
    @GET("/api/v1/items") Call<List<PublicPost>> stream(
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v1/users/{url_name}/items") Call<List<PublicPost>> userItems(
        @Path("url_name") String userId,
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v1/users/{url_name}/stocks") Call<List<PublicPost>> userStockedItems(
        @Path("url_name") String userId,
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v1/users/{url_name}/following_tags") Call<List<PublicTag>> userFollowingTags(
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

    @GET("/api/v2/authenticated_user/items") Call<List<Article>> myItems(
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v2/users/{user_id}/following_tags") Call<List<Tag>> tags(
        @Path("item_id") String id,
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v2/items") Call<List<Article>> searchItems(
        @Query("page") int page,
        @Query("per_page") int limit,
        @Query("query") String query
    );

    @GET("/api/v2/users/{user_id}/items") Call<List<Article>> userItems(
        @Path("user_id") String userId,
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v2/users/{user_id}/stocks") Call<List<Article>> userStockedItems(
        @Path("user_id") String userId,
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v2/items/{item_id}") Call<Article> itemDetail(
        @Path("item_id") String id
    );

    @GET("/api/v2/items/{item_id}/comments") Call<ArrayList<Comment>> comments(
        @Path("item_id") String id
    );

    @GET("/api/v2/tags") Call<List<Tag>> tags(
        @Query("page") int page,
        @Query("per_page") int limit,
        @Query("sort") String sort
    );

    @GET("/api/v2/users/{user_id}/following_tags") Call<List<Tag>> userFollowingTags(
        @Path("user_id") String userId,
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v2/tags/{tag_id}/items") Call<List<Article>> tagItems(
        @Path("tag_id") String tagName,
        @Query("page") int page,
        @Query("per_page") int limit
    );

    @GET("/api/v2/users/{user_id}") Call<User> user(@Path("user_id") String userName);

    @GET("/api/v2/items/{item_id}/stock") Call<Void> getStock(
        @Path("item_id") String id
    );

    @PUT("/api/v2/items/{item_id}/stock") Call<Void> putStock(
        @Path("item_id") String id
    );

    @DELETE("/api/v2/items/{item_id}/stock") Call<Void> deleteStock(
        @Path("item_id") String id
    );

    // Following action
    @GET("/api/v2/users/{user_id}/following") Call<Void> getFollow(
        @Path("user_id") String userId
    );

    @PUT("/api/v2/users/{user_id}/following") Call<Void> putFollow(
        @Path("user_id") String userId
    );

    @DELETE("/api/v2/users/{user_id}/following") Call<Void> deleteFollow(
        @Path("user_id") String userId
    );

    // Success POST will return code 201
    @POST("/api/v2/items/{item_id}/comments") Call<Comment> postComment(
        @Path("item_id") String itemId,
        @Body PostCommentRequest body
    );

    // Success code: 204
    @DELETE("/api/v2/comments/{comment_id}") Call<Void> deleteComment(
        @Path("comment_id") String commentId
    );

    @PATCH("/api/v2/comments/{comment_id}") Call<Comment> patchComment(
        @Path("comment_id") String commentId,
        @Body PostCommentRequest body
    );
  }
}
