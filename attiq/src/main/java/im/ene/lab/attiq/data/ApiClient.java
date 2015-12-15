package im.ene.lab.attiq.data;

import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;

import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.request.AccessTokenRequest;
import im.ene.lab.attiq.data.response.AccessToken;
import im.ene.lab.attiq.data.response.Item;
import im.ene.lab.attiq.data.vault.PublicItem;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.PrefUtil;
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

  private static final Api.Public PUBLIC;
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

    PUBLIC = RETROFIT.create(Api.Public.class);
    ITEMS = RETROFIT.create(Api.Items.class);
    SELF = RETROFIT.create(Api.Me.class);
  }

  public static OkHttpClient client() {
    return HTTP_CLIENT;
  }

  public static String authCallback() {
    return "https://qiita.com/api/v2/oauth/authorize" +
        "?client_id=bfd0c62e1d881bf1eff108554cbc3cbb389bad6f" +
        "&scope=read_qiita+write_qiita&state=299792459";
  }

  public static Call<List<PublicItem>> stream(@Nullable Long bottomId) {
    if (bottomId != null) {
      return PUBLIC.stream(bottomId, "id");
    } else {
      return PUBLIC.stream();
    }
  }

  public static Call<List<Item>> items(int page, int pageLimit, String query) {
    return ITEMS.items(page, pageLimit, query);
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
}
