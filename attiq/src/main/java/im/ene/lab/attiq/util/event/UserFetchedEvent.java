package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.model.two.User;

/**
 * Created by eneim on 12/14/15.
 */
public class UserFetchedEvent extends Event {

  public final User user;

  public UserFetchedEvent(boolean isSuccess, @Nullable Error error, User user) {
    super(isSuccess, error);
    this.user = user;
  }
}
