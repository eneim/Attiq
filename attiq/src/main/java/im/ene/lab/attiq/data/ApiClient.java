package im.ene.lab.attiq.data;

import com.squareup.okhttp.OkHttpClient;

import im.ene.lab.attiq.data.response.Item;
import im.ene.lab.attiq.util.JsonUtil;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
public final class ApiClient {

  private static final OkHttpClient HTTP_CLIENT;
  private static final Retrofit RETROFIT;

  private static final Api.Items ITEMS;

  static {
    HTTP_CLIENT = new OkHttpClient();
    RETROFIT = new Retrofit.Builder()
        .baseUrl(Api.BASE_URL)
        .client(HTTP_CLIENT)
        .addConverterFactory(GsonConverterFactory.create(JsonUtil.gson()))
        .build();

    ITEMS = RETROFIT.create(Api.Items.class);
  }

  public static OkHttpClient client() {
    return HTTP_CLIENT;
  }

  public static Call<List<Item>> items(int page, int pageLimit, String query) {
    return ITEMS.items(page, pageLimit, query);
  }
}
