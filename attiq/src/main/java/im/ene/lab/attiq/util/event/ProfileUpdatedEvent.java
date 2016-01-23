package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.model.local.RProfile;

/**
 * Created by eneim on 1/14/16.
 */
public class ProfileUpdatedEvent extends Event {

  public final RProfile profile;

  public ProfileUpdatedEvent(boolean success, @Nullable Error error, RProfile profile) {
    super(success, error);
    this.profile = profile;
  }
}
