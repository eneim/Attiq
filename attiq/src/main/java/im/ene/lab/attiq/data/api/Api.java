package im.ene.lab.attiq.data.api;

import im.ene.lab.attiq.data.api.open.FeedItem;
import im.ene.lab.attiq.data.api.open.Profile;
import im.ene.lab.attiq.data.api.v1.response.PublicItem;
import im.ene.lab.attiq.data.api.v2.request.AccessTokenRequest;
import im.ene.lab.attiq.data.api.v2.response.AccessToken;
import im.ene.lab.attiq.data.api.v2.response.Article;
import im.ene.lab.attiq.data.api.v2.response.Comment;
import im.ene.lab.attiq.data.api.v2.response.Tag;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
interface Api {

  String BASE_URL = "https://qiita.com";

  interface Open {

    @GET("/api/public") Call<List<PublicItem>> stream(
        @Query("before") Long id,
        @Query("type") String type
    );

    @GET("/api/public") Call<List<PublicItem>> stream();

    @GET("/api/track") Call<List<FeedItem>> feed(
        @Query("max_created_at") Long maxCreatedAt
    );
  }

  interface Items {

    // No token only
    @GET("/api/v1/items") Call<List<PublicItem>> stream(
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
  }

  interface Me {

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
  }
}
