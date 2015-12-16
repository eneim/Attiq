package im.ene.lab.attiq.data.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.Profile;

/**
 * Created by eneim on 12/14/15.
 */
public class FetchedMasterEvent extends Event {

  private Profile profile;

  public FetchedMasterEvent(boolean isSuccess, @Nullable Error error, Profile profile) {
    super(isSuccess, error);
    this.profile = profile;
  }

  public Profile getProfile() {
    return profile;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }
}
