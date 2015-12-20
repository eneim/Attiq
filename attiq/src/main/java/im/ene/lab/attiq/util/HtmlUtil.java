package im.ene.lab.attiq.util;

/**
 * Created by eneim on 12/18/15.
 */
public class HtmlUtil {

  private static final String[] HEADERS = {
      "h0", "h1", "h2", "h3", "h4", "h5", "h6"
  };

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
