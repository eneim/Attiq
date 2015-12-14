package im.ene.lab.attiq.util;

import im.ene.lab.attiq.Attiq;

/**
 * Created by eneim on 12/13/15.
 */
public class PrefUtil {

  private static final String PREF_CURRENT_TOKEN = "attiq_preference_current_token";

  public static void setCurrentToken(String token) {
    Attiq.pref().edit().putString(PREF_CURRENT_TOKEN, token).apply();
  }

  public static String getCurrentToken() {
    return Attiq.pref().getString(PREF_CURRENT_TOKEN, null);
  }
}
