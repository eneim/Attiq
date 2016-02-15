package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;
import im.ene.lab.attiq.data.model.two.AccessToken;

/**
 * Created by eneim on 12/13/15.
 */
public class AccessTokenEvent extends Event {

  public final AccessToken token;

  public AccessTokenEvent(@Nullable String tag, boolean success, @Nullable Error error,
      AccessToken token) {
    super(tag, success, error);
    this.token = token;
  }
}
