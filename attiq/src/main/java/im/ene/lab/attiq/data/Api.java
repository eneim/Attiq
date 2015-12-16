package im.ene.lab.attiq.data;

import im.ene.lab.attiq.data.request.AccessTokenRequest;
import im.ene.lab.attiq.data.response.AccessToken;
import im.ene.lab.attiq.data.response.Comment;
import im.ene.lab.attiq.data.response.Article;
import im.ene.lab.attiq.data.vault.PublicItem;
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

  interface Public {

    @GET("api/public") Call<List<PublicItem>> stream(@Query("before") long id,
                                                     @Query("type") String type);

    @GET("api/public") Call<List<PublicItem>> stream();
  }

  interface Items {

    @GET("/api/v2/items") Call<List<Article>> items(@Query("page") int page,
                                                 @Query("per_page") int limit,
                                                 @Query("query") String query);

    @GET("/api/v2/items/{item_id}") Call<Article> itemDetail(@Path("item_id") String id);

    @GET("/api/v2/items/{item_id}/comments") Call<List<Comment>> comments(
        @Path("item_id") String id
    );
  }

  interface Me {

    @POST("/api/v2/access_tokens") Call<AccessToken> accessToken(@Body AccessTokenRequest request);

    @GET("/api/v2/authenticated_user") Call<Profile> me();
  }
}
