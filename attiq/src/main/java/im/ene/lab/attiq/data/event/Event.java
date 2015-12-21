package im.ene.lab.attiq.data.event;

import android.support.annotation.Nullable;

/**
 * Created by eneim on 12/13/15.
 */
public class Event {

  public final boolean success;

  @Nullable public final Error error;

  public Event(boolean success, @Nullable Error error) {
    this.success = success;
    this.error = error;
  }

  public static class Error {

    public final int code;

    public final String message;

    public Error(int code, String message) {
      this.code = code;
      this.message = message;
    }

    public static final int ERROR_UNKNOWN = 0;
  }
}
