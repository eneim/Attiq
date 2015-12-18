package im.ene.lab.attiq.util;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by eneim on 12/18/15.
 */
public class HtmlUtil {

  private static final String H0 = "h0";

  private static final String H1 = "h1";

  private static final String H2 = "h2";

  private static final String H3 = "h3";

  private static final String H4 = "h4";

  private static final String H5 = "h5";

  private static final String H6 = "h6";

  private static final String[] HEADERS = {
      H0, H1, H2, H3, H4, H5, H6
  };

  @StringDef({
      H0, H1, H2, H3, H4, H5, H6
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface Header {
  }

  public static int getHeaderLevel(String header) {
    int level = -1;
    while (level++ < HEADERS.length) {
      if (HEADERS[level].equals(header)) {
        return level + 1;
      }
    }

    return level;
  }
}
