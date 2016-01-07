package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.two.Profile;

/**
 * Created by eneim on 12/14/15.
 */
public class ProfileFetchedEvent extends Event {

  public final Profile profile;

  public ProfileFetchedEvent(boolean isSuccess, @Nullable Error error, Profile profile) {
    super(isSuccess, error);
    this.profile = profile;
  }
}
