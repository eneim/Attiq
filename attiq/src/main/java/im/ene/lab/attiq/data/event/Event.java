package im.ene.lab.attiq.data.event;

import android.support.annotation.Nullable;

/**
 * Created by eneim on 12/13/15.
 */
public class Event {

  private boolean isSuccess;

  @Nullable private Error error;

  public Event(boolean isSuccess, @Nullable Error error) {
    this.isSuccess = isSuccess;
    this.error = error;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  @Nullable public Error getError() {
    return error;
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
