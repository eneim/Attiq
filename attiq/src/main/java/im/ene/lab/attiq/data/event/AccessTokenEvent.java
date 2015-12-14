package im.ene.lab.attiq.data.event;

import im.ene.lab.attiq.data.response.AccessToken;

/**
 * Created by eneim on 12/13/15.
 */
public class AccessTokenEvent extends Event {

  private AccessToken accessToken;

  public AccessTokenEvent(boolean isSuccess, AccessToken accessToken) {
    super(isSuccess);
    this.accessToken = accessToken;
  }

  public AccessToken getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(AccessToken accessToken) {
    this.accessToken = accessToken;
  }
}
