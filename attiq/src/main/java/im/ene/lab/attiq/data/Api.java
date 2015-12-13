package im.ene.lab.attiq.data;

import im.ene.lab.attiq.data.response.Item;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
interface Api {
  String BASE_URL = "https://qiita.com";

  interface Items {

    @GET("/api/v2/items")
    Call<List<Item>> items(@Query("page") int page,
                           @Query("per_page") int limit,
                           @Query("query") String query);
  }
}
