package im.ene.lab.attiq.data.request;

/**
 * Created by eneim on 12/13/15.
 */
public class BaseRequest {

  public static class Headers {

    public static final String AUTHORIZATION = "Authorization";

    public static String authorization(String token) {
      return "Bearer " + token;
    }
  }
}
