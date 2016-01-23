package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.model.two.AccessToken;

/**
 * Created by eneim on 12/13/15.
 */
public class AccessTokenEvent extends Event {

  public final AccessToken accessToken;

  public AccessTokenEvent(boolean isSuccess, @Nullable Error error, AccessToken accessToken) {
    super(isSuccess, error);
    this.accessToken = accessToken;
  }
}
