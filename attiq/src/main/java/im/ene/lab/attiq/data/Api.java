package im.ene.lab.attiq.data;

import im.ene.lab.attiq.data.request.AccessTokenRequest;
import im.ene.lab.attiq.data.response.AccessToken;
import im.ene.lab.attiq.data.response.Item;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
interface Api {
  String BASE_URL = "https://qiita.com";

  interface Items {

    @GET("/api/v2/items") Call<List<Item>> items(@Query("page") int page,
                                                 @Query("per_page") int limit,
                                                 @Query("query") String query);
  }

  interface Me {

    @POST("/api/v2/access_tokens") Call<AccessToken> accessToken(@Body AccessTokenRequest request);

    @GET("/api/v2/authenticated_user") Call<Master> me(@Header("Authorization") String token);
  }
}
