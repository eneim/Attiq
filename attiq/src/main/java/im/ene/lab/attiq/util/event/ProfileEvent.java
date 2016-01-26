package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.model.two.Profile;

/**
 * Created by eneim on 12/14/15.
 */
public class ProfileEvent extends Event {

  public final Profile profile;

  @Deprecated
  public ProfileEvent(boolean isSuccess, @Nullable Error error, Profile profile) {
    this(ProfileEvent.class.getSimpleName(), isSuccess, error, profile);
  }

  public ProfileEvent(@Nullable String tag, boolean success, @Nullable Error error, Profile
      profile) {
    super(tag, success, error);
    this.profile = profile;
  }
}
