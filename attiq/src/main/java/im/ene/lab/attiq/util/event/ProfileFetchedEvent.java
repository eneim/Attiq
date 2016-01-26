package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.model.two.User;

/**
 * Created by eneim on 12/14/15.
 */
public class ProfileFetchedEvent extends Event {

  public final User user;

  public ProfileFetchedEvent(@Nullable String tag, boolean success, @Nullable Error error,
                             User user) {
    super(tag, success, error);
    this.user = user;
  }
}
