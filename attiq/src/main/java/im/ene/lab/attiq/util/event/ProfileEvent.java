package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.model.two.Profile;

/**
 * Created by eneim on 12/14/15.
 */
public class ProfileEvent extends Event {

  public final Profile profile;

  public ProfileEvent(boolean isSuccess, @Nullable Error error, Profile profile) {
    super(isSuccess, error);
    this.profile = profile;
  }
}
