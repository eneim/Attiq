package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

/**
 * Created by eneim on 12/13/15.
 * <p/>
 * Support {@link de.greenrobot.event.EventBus}
 */
public class Event {

  public final String tag;

  public boolean success;

  @Nullable public Error error;

  Event(String tag) {
    this.tag = tag;
  }

  public Event(@Nullable String tag, boolean success, @Nullable Error error) {
    this.tag = tag;
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

    @Override public String toString() {
      return "Error{" +
          "code=" + code +
          ", message='" + message + '\'' +
          '}';
    }
  }

  @Override public String toString() {
    return "Event{" +
        "tag='" + tag + '\'' +
        ", success=" + success +
        ", error=" + error +
        '}';
  }

}
