package im.ene.lab.attiq.data.api;

/**
 * Created by eneim on 12/21/15.
 */
public class Header {

  public static class Response {

    public static final String LINK = "Link";

    public static final String RATE_LIMTE = "Rate-Limit";

    public static final String RATE_REMAINING = "Rate-Remaining";

    public static final String RATE_RESET = "Rate-Reset";

    public static final String TOTAL_COUNT = "Total-Count";
  }

  public static class Request {

    public static final String AUTHORIZATION = "Authorization";

    public static String authorization(String token) {
      return "Bearer " + token;
    }
  }
}
