package im.ene.lab.attiq.util;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.data.api.base.Header;

import java.io.IOException;

/**
 * Created by eneim on 12/13/15.
 */
public class PrefUtil {

  private static final String PREF_CURRENT_TOKEN = "attiq_preference_current_token";

  private static final String PREF_FIRST_START_FLAG = "attiq_preference_flag_first_start";

  private static final AuthInterceptor sInterceptor = new AuthInterceptor();

  public static Interceptor authInterceptor() {
    return sInterceptor;
  }

  public static void setCurrentToken(String token) {
    Attiq.pref().edit().putString(PREF_CURRENT_TOKEN, token).apply();
  }

  public static String getCurrentToken() {
    return Attiq.pref().getString(PREF_CURRENT_TOKEN, null);
  }

  public static boolean isFirstStart() {
    return Attiq.pref().getBoolean(PREF_FIRST_START_FLAG, true);
  }

  public static void setFirstStart(boolean isFirstStart) {
    Attiq.pref().edit().putBoolean(PREF_FIRST_START_FLAG, isFirstStart).apply();
  }

  /**
   * ref: http://stackoverflow.com/a/27868976/1553254
   */
  static class AuthInterceptor implements Interceptor {

    @Override public Response intercept(Chain chain) throws IOException {
      Request.Builder requestBuilder = chain.request().newBuilder();
      if (!UIUtil.isEmpty(PrefUtil.getCurrentToken())) {
        requestBuilder.addHeader(Header.Request.AUTHORIZATION,
            Header.Request.authorization(PrefUtil.getCurrentToken()));
      }

      return chain.proceed(requestBuilder.build());
    }
  }
}
